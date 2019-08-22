package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.TokenWalletContract
import com.template.states.IssueOrderState
import com.template.states.TokenWalletState
import net.corda.core.contracts.Command
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class TokenIssueOrderFlow(private val amount: Long,
                          private val currency: String,
                          private val issuer: String) : Test() {


    @Suspendable
    override fun call(): SignedTransaction {

        val signedTransaction: SignedTransaction = verifyAndSign(issue())
        val session = initiateFlow(stringToParty(issuer))
        val transactionSigned: SignedTransaction = collectSignature(signedTransaction, listOf(session))
        return recordTransactionWithParty(transactionSigned, session)

    }

    private fun outState(): IssueOrderState {
        return IssueOrderState(
                amount,
                currency,
                stringToParty(issuer),
                listOf(ourIdentity, stringToParty(issuer))
        )
    }

    private fun issue() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val cmd = Command(TokenWalletContract.Commands.Issue(), listOf(ourIdentity.owningKey, stringToParty(issuer).owningKey))
        addOutputState(outState())
        addCommand(cmd)
    }

}



@InitiatedBy(TokenIssueOrderFlow::class)
class TokenIssueOrderFlowResponder(val flowSession: FlowSession): FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a transaction" using (output is TokenWalletState)
            }
        }
        val txWeJustSignedId = subFlow(signedTransactionFlow)
        return subFlow(ReceiveFinalityFlow(otherSideSession = flowSession, expectedTxId = txWeJustSignedId.id))
    }
}