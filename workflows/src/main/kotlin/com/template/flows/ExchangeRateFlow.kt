package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.TokenContract
import com.template.states.TokenWalletState
import net.corda.core.contracts.Amount
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@StartableByRPC
class ExchangeRateFlow(
        private val convert_from: String,
        private val to: String,
        private val ownerId: String) : Test()
{
    @Suspendable
    override fun call(): SignedTransaction
    {
        return recordTransactionsWithoutOtherParty(verifyAndSign(registration()))
    }
    private fun inputStateAndRef(id: UniqueIdentifier): StateAndRef<TokenWalletState> {
        val criteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(id))
        return serviceHub.vaultService.queryBy<TokenWalletState>(criteria = criteria).states.single()
    }
    private fun outState(): TokenWalletState
    {
        val input = inputStateAndRef(stringToLinearId(ownerId)).state.data
        return TokenWalletState(
                input.username,
                input.password,
                newWallet(),
                listOf(ourIdentity),
                input.linearId
        )
    }

    private fun newWallet(): MutableList<Amount<TokenType>>
    {
        print (getExchangeRate())
        print (" ")
        val input = inputStateAndRef (stringToLinearId(ownerId)).state.data
        val wallet = input.wallet.toMutableList()
        val wal = input.wallet.find{ l -> l.token.tokenIdentifier == convert_from}
        val wal2 = input.wallet.find{ l -> l.token.tokenIdentifier == to}
        print (wal!!.quantity.toBigDecimal())
        print (" ")
        val newValue = when (convert_from) {
            "PHP" -> (((wal.quantity)*0.01).div(getExchangeRate())).toBigDecimal()
            "USD" -> (((wal.quantity)*0.01).times(getExchangeRate())).toBigDecimal()
            else -> null
        }
        print (newValue)
        print (" ")
        val new = when (to) {
            "PHP" -> wal2?.plus(Amount.fromDecimal(newValue!!,TokenType("PHP", 2)))
            "USD" -> wal2?.plus(Amount.fromDecimal(newValue!!,TokenType("USD", 2)))
            else -> null
        }
        print(new)
        print (" ")
        val sub = wal.minus(wal)
        val index = input.wallet.indexOf(wal)
        val index2 = input.wallet.indexOf(wal2)
        wallet.removeAt(index2)
        wallet.add(index2,new!!)
        wallet.removeAt(index)
        wallet.add(index,sub)
        return wallet
    }

    private fun registration() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val cmd = Command(TokenContract.Commands.Exchange(), listOf(ourIdentity.owningKey))
        addInputState(inputStateAndRef (stringToLinearId(ownerId)))
        addOutputState(outState())
        addCommand(cmd)
    }
    private fun getExchangeRate(): Double {
        val url = "https://api.exchangeratesapi.io/latest?base=USD&symbols=PHP,USD"
        val response = khttp.get(url)
        try {
            return response.jsonObject.optJSONObject("rates").getDouble("PHP")
        } catch (e: Exception) {
            throw IllegalArgumentException()
        }
    }
}