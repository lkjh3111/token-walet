package com.template.states

import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.template.TokenWalletContract
import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

@BelongsToContract(TokenWalletContract::class)
data class IssueOrderState(val amount: Long,
                           val currency: String,
                           val issuer: Party,
                           val approve_request: Boolean,
                           override val participants: List<Party>,
                           override val linearId: UniqueIdentifier = UniqueIdentifier()): LinearState