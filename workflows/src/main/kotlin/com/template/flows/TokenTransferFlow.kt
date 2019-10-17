package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.money.FiatCurrency
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveFungibleTokens
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.TokenWalletContract
import com.template.states.IssueOrderState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class TokenTransferFlow(private val approve_request: Boolean,
                        private val txId: String) : Test() {


    @Suspendable
    override fun call(): SignedTransaction {
        val platform = stringToParty("Platform")
        val inputStateRef = inputStateAndRef (stringToLinearId(txId))
        val input = inputStateRef.state.data
        val token = FiatCurrency.getInstance(input.currency)
        val signedTransaction: SignedTransaction = verifyAndSign(issue())
        val session = initiateFlow(platform)
        val transactionSigned: SignedTransaction = collectSignature(signedTransaction, listOf(session))
        if (approve_request){
            subFlow(MoveFungibleTokens(input.amount of token,  platform))
        }
        return recordTransactionWithParty(transactionSigned,session)
    }

    private fun inputStateAndRef(id: UniqueIdentifier): StateAndRef<IssueOrderState> {
        val criteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(id))
        return serviceHub.vaultService.queryBy<IssueOrderState>(criteria = criteria).states.single()
    }

    private fun outState(): IssueOrderState {
        val inputStateRef = inputStateAndRef (stringToLinearId(txId))
        val input = inputStateRef.state.data
        return IssueOrderState(
                input.amount,
                input.currency,
                approve_request,
                input.participants
        )
    }

    private fun issue() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val participants = if (approve_request){
            listOf()
        }else {
            listOf(stringToParty("Platform"))
        }
        val cmd = Command(TokenWalletContract.Commands.Transfer(), listOf(ourIdentity.owningKey, stringToParty("Platform").owningKey))
        addInputState(inputStateAndRef(stringToLinearId(txId)))
        addOutputState(outState().copy(participants = participants))
        addCommand(cmd)
    }

}

@InitiatedBy(TokenTransferFlow::class)
class TokenTransferResponder(val flowSession: FlowSession): FlowLogic<SignedTransaction>() {
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