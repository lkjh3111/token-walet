package com.template.states

import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.template.TokenWalletContract
import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

@BelongsToContract(TokenWalletContract::class)
data class PreorderState(val amount: Long,
                         val currency: String,
                         val ownerId: String,
                         override val participants: List<Party>,
                         override val linearId: UniqueIdentifier = UniqueIdentifier()): LinearState