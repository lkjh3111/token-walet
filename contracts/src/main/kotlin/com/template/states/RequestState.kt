package com.template.states

import com.template.TokenWalletContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

@BelongsToContract(TokenWalletContract::class)
data class RequestState(val requestAccessTo: Party,
                        val requestStatus: Boolean,
                        override val participants: List<Party>,
                        override val linearId: UniqueIdentifier = UniqueIdentifier()): LinearState