package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.money.FiatCurrency
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveFungibleTokens
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemFungibleTokens
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.TokenWalletContract
import com.template.states.PreorderState
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
class AcceptPreOrderFlow(private val txId: String) : Test()
{
    @Suspendable
    override fun call(): SignedTransaction
    {
        val input = inputStateAndRef (stringToLinearId(txId)).state.data
        val token = FiatCurrency.getInstance(input.currency)
        if (input.currency == "PHP"){
            subFlow(RedeemFungibleTokens(input.amount of token,  stringToParty("BSP")))
        }
        if (input.currency == "USD"){
            subFlow(RedeemFungibleTokens(input.amount of token,  stringToParty("USB")))
        }
        return recordTransactionsWithoutOtherParty(verifyAndSign(accept()))
    }

    private fun inputStateAndRef(id: UniqueIdentifier): StateAndRef<PreorderState> {
        val criteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(id))
        return serviceHub.vaultService.queryBy<PreorderState>(criteria = criteria).states.single()
    }

    private fun inputStateRef(id: UniqueIdentifier): StateAndRef<TokenWalletState> {
        val criteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(id))
        return serviceHub.vaultService.queryBy<TokenWalletState>(criteria = criteria).states.single()
    }

    private fun outState(): TokenWalletState
    {
        val input = inputStateAndRef (stringToLinearId(txId)).state.data
        val input1 = inputStateRef(stringToLinearId(input.ownerId)).state.data
        return TokenWalletState(
               input1.username,
                input1.password,
                input1.wallet,
                input1.participants,
                input1.linearId
        )
    }
    private fun accept() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val input = inputStateAndRef (stringToLinearId(txId)).state.data
        val input1 = inputStateRef(stringToLinearId(input.ownerId)).state.data
        val token = FiatCurrency.getInstance(input.currency)
        val wallet = input1.wallet
        val new = wallet.find{ l -> l.token.tokenIdentifier == input.currency}?.plus(input.amount of token)
        print (new)
        val cmd = Command(TokenWalletContract.Commands.Accept(), listOf(ourIdentity.owningKey))
        addInputState(inputStateAndRef (stringToLinearId(txId)))
        addInputState(inputStateRef(stringToLinearId(input.ownerId)))
        addOutputState(outState().copy(wallet = newWallet()))
        addCommand(cmd)
    }
    private fun newWallet(): MutableList<Amount<TokenType>>
    {
        val input = inputStateAndRef (stringToLinearId(txId)).state.data
        val input1 = inputStateRef(stringToLinearId(input.ownerId)).state.data
        val token = FiatCurrency.getInstance(input.currency)
        val wallet = input1.wallet.toMutableList()
        val wal = input1.wallet.find{ l -> l.token.tokenIdentifier == input.currency}
        val new = wal?.plus(input.amount of token)
        val index = input1.wallet.indexOf(wal)
        wallet.removeAt(index)
        wallet.add(index,new!!)
        return wallet
    }


}
