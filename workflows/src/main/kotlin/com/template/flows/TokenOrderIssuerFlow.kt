package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.TokenWalletContract
import com.template.states.IssueOrderState
import com.template.states.TokenWalletState
import net.corda.core.contracts.Command
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class TokenOrderIssuerFlow(private val amount: Long,
                          private val currency: String) : Test() {


    @Suspendable
    override fun call(): SignedTransaction {
        val issuer = when {
            currency == "PHP" -> stringToParty("BSP")
            currency == "USD" -> stringToParty("USB")
            else -> null
        }

        val signedTransaction: SignedTransaction = verifyAndSign(issue(issuer!!))
        val session = initiateFlow(issuer!!)
        val transactionSigned: SignedTransaction = collectSignature(signedTransaction, listOf(session))
        return recordTransactionWithParty(transactionSigned, session)

    }

    private fun issue(issuer: Party) = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val cmd = Command(TokenWalletContract.Commands.Issue(), listOf(ourIdentity.owningKey, issuer.owningKey))
        addOutputState(outState(issuer))
        addCommand(cmd)
    }

    private fun outState(issuer: Party): IssueOrderState {
        return IssueOrderState(
                amount,
                currency,
                false,
                listOf(ourIdentity, issuer)
        )
    }

}

@InitiatedBy(TokenOrderIssuerFlow::class)
class TokenOrderIssuerFlowResponder(val flowSession: FlowSession): FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a transaction" using (output is IssueOrderState)
            }
        }
        val txWeJustSignedId = subFlow(signedTransactionFlow)
        return subFlow(ReceiveFinalityFlow(otherSideSession = flowSession, expectedTxId = txWeJustSignedId.id))
    }
}