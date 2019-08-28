package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.StatesToRecord
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction

@InitiatingFlow
@StartableByRPC
class Broadcast(private val stx: SignedTransaction) : FlowLogic<Unit>() {
    @Suspendable
    override fun call(){
//        val str =  CordaX500Name( "BSP", "New York", "US")
//
//        val stateref = QueryCriteria.LinearStateQueryCriteria().participants!!.find{l -> l.nameOrNull() == str}
//        val state = stateref.
        val counterParty = stringToParty("BSP")
        val session = initiateFlow(counterParty)
        subFlow(SendTransactionFlow(session, stx))
    }
    private fun stringToParty(party: String): Party {
        return serviceHub.identityService.partiesFromName(party, false).singleOrNull()
                ?: throw IllegalArgumentException("No match found for $party")
    }
}

@InitiatedBy(Broadcast::class)
class BroadcastResponder(val flowSession: FlowSession): FlowLogic<SignedTransaction>() {
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