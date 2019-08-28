package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.money.FiatCurrency
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.TokenWalletContract
import com.template.states.IssueOrderState
import com.template.states.RequestState
import com.template.states.TokenWalletState
import net.corda.core.contracts.Amount
import net.corda.core.contracts.Command
import net.corda.core.contracts.Requirements.using
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class RequestAcessFlow(private val requestAccessTo: String,
                       private val approveRequest: String) : Test()
{
    @Suspendable
    override fun call(): SignedTransaction
    {
        return recordTransactionsWithoutOtherParty(verifyAndSign(request()))
    }

    private fun outState(): RequestState
    {
        return RequestState(
                stringToParty(requestAccessTo),
                approveRequest,
                listOf(ourIdentity,stringToParty(requestAccessTo))

        )
    }

    private fun request() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val cmd = Command(TokenWalletContract.Commands.Register(), listOf(ourIdentity.owningKey))
        addOutputState(outState())
        addCommand(cmd)
    }
}

@InitiatedBy(RequestAcessFlow::class)
class RequestAccessFlowResponder(val flowSession: FlowSession): FlowLogic<SignedTransaction>() {
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