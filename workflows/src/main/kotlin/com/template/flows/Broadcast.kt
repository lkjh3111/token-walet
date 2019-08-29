package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.states.RequestState
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.StatesToRecord
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction

@InitiatingFlow
@StartableByRPC
class Broadcast(private val stx: SignedTransaction) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val sessions = cast().map { initiateFlow(it) }
        sessions.forEach { subFlow(SendTransactionFlow(it, stx)) }
    }

    private fun stringToParty(party: String): Party {
        return serviceHub.identityService.partiesFromName(party, false).singleOrNull()
                ?: throw IllegalArgumentException("No match found for $party")
    }

    private fun cast(): List<Party> {
        val stateRef1 = serviceHub.vaultService.queryBy(RequestState::class.java).states
        return if (stateRef1 != emptyList<StateAndRef<RequestState>>()) {
            val criteria = QueryCriteria.LinearStateQueryCriteria(participants = listOf(stringToParty("BSP")))
            val stateRef = serviceHub.vaultService.queryBy<RequestState>(criteria = criteria).states.single()
            val input = stateRef.state.data
            val criteria1 = QueryCriteria.LinearStateQueryCriteria(participants = listOf(stringToParty("USB")))
            val stateRef1 = serviceHub.vaultService.queryBy<RequestState>(criteria = criteria1).states.single()
            val input1 = stateRef1.state.data
            when {
                input.requestStatus -> listOf(input.participants[0])
                input1.requestStatus -> listOf(input1.participants[0])
                else -> listOf()
            }
        } else {
            listOf()
        }
    }
}

@InitiatedBy(Broadcast::class)
class BroadcastResponder(private val flowSession: FlowSession): FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call():SignedTransaction {
        val flow = ReceiveTransactionFlow(
                otherSideSession = flowSession,
                checkSufficientSignatures = true,
                statesToRecord = StatesToRecord.ALL_VISIBLE
        )
        return subFlow(flow)
    }
}