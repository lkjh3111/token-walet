package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.money.FiatCurrency
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.TokenWalletContract
import com.template.states.TokenWalletState
import jdk.nashorn.internal.parser.Token
import net.corda.core.contracts.Amount
import net.corda.core.contracts.Command
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder



@StartableByRPC
class TokenRegisterFlow(
                private val username: String,
                private val password: String,
                private val quantity: List<Long>,
                private val currency: List<String>) : Test()
{
    @Suspendable
    override fun call():SignedTransaction
    {
        return recordTransactionsWithoutOtherParty(verifyAndSign(registration()))
    }

    private fun outState(): TokenWalletState
    {
        return TokenWalletState(
                username,
                password,
                newWallet(),
                listOf(ourIdentity)
        )
    }

    private fun newWallet(): MutableList<Amount<TokenType>>
    {
        val wallet = mutableListOf<Amount<TokenType>>()
        for ((index, value) in quantity.withIndex()) {
            val token = FiatCurrency.getInstance(currency[index])
            wallet.add(index,value of token)
        }
        return wallet
    }

    private fun registration() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val cmd = Command(TokenWalletContract.Commands.Register(), listOf(ourIdentity.owningKey))
        addOutputState(outState())
        addCommand(cmd)
    }
}




