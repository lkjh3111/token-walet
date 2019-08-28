package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.TokenWalletContract
import com.template.states.IssueOrderState
import com.template.states.RequestState
import net.corda.core.contracts.Command
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class RequestAccessFlow(private val requestAccessTo: String) : Test()
{
    @Suspendable
    override fun call(): SignedTransaction
    {
        val signedTransaction: SignedTransaction = verifyAndSign(request())
        val session = initiateFlow(stringToParty(requestAccessTo))
        val transactionSigned: SignedTransaction = collectSignature(signedTransaction, listOf(session))
        return recordTransactionWithParty(transactionSigned, session)
    }
    private fun outState(): RequestState
    {
        return RequestState(
                stringToParty(requestAccessTo),
                false,
                listOf(ourIdentity,stringToParty(requestAccessTo))
        )
    }

    private fun request() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val cmd = Command(TokenWalletContract.Commands.RequestAccess(), listOf(ourIdentity.owningKey, stringToParty(requestAccessTo).owningKey))
        addOutputState(outState())
        addCommand(cmd)
    }
}

@InitiatedBy(RequestAccessFlow::class)
class RequestAccessFlowResponder(val flowSession: FlowSession): FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a transaction" using (output is RequestState)
            }
        }
        val txWeJustSignedId = subFlow(signedTransactionFlow)
        return subFlow(ReceiveFinalityFlow(otherSideSession = flowSession, expectedTxId = txWeJustSignedId.id))
    }
}