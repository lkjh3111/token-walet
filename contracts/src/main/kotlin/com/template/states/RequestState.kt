package com.template.states

import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

data class RequestState(val requestAccessTo: Party,
                        val requestStatus: String,
                         override val participants: List<Party>,
                         override val linearId: UniqueIdentifier = UniqueIdentifier()): LinearState