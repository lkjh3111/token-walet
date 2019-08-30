package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.RequestContract
import com.template.states.RequestState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class ApproveRequestFlow(private val approveRequest: Boolean,
                         private val linearId: String) : Test() {
    @Suspendable
    override fun call(): SignedTransaction
    {
        val input = inputStateAndRef(stringToLinearId(linearId)).state.data
        val signedTransaction: SignedTransaction = verifyAndSign(request())
        val session = initiateFlow(input.participants[0])
        val transactionSigned: SignedTransaction = collectSignature(signedTransaction, listOf(session))
        return recordTransactionWithParty(transactionSigned, session)
    }

    private fun inputStateAndRef(id: UniqueIdentifier): StateAndRef<RequestState> {
        val criteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(id))
        return serviceHub.vaultService.queryBy<RequestState>(criteria = criteria).states.single()
    }

    private fun outState(): RequestState
    {
        val input = inputStateAndRef(stringToLinearId(linearId)).state.data
        return RequestState(
                input.requestAccessTo,
                approveRequest,
                input.participants,
                input.linearId
        )
    }

    private fun request() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val input = inputStateAndRef(stringToLinearId(linearId)).state.data
        val cmd = Command(RequestContract.Commands.RequestAccess(), listOf(ourIdentity.owningKey, input.participants[0].owningKey))
        addInputState(inputStateAndRef(stringToLinearId(linearId)))
        addOutputState(outState())
        addCommand(cmd)
    }
}

@InitiatedBy(ApproveRequestFlow::class)
class ApproveRequestFlowResponder(val flowSession: FlowSession): FlowLogic<SignedTransaction>() {
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