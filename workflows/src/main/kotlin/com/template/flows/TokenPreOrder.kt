package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.TokenWalletContract
import com.template.states.PreorderState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@StartableByRPC
class TokenPreOrder(
        private val amount: Long,
        private val currency: String,
        private val ownerId: String) : Test()
{
    @Suspendable
    override fun call():SignedTransaction
    {
        return recordTransactionsWithoutOtherParty(verifyAndSign(preorder()))
    }

    private fun outState(): PreorderState
    {
        return PreorderState(

                amount,
                currency,
                ownerId,
                listOf(ourIdentity)

        )

    }
    private fun preorder() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val cmd = Command(TokenWalletContract.Commands.Preorder(), listOf(ourIdentity.owningKey))
        addOutputState(outState())
        addCommand(cmd)
    }


}



