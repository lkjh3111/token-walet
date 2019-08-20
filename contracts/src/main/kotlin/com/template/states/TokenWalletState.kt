package com.template.states

import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType
import com.template.TokenWalletContract
import jdk.nashorn.internal.parser.TokenType
import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

@BelongsToContract(TokenWalletContract::class)
data class TokenWalletState(val username: String,
                            val password: String,
                            val requested_amount: Amount<com.r3.corda.lib.tokens.contracts.types.TokenType>?,
                            val wallet: MutableList<Amount<com.r3.corda.lib.tokens.contracts.types.TokenType>>,
                            override val participants: List<Party>,
                            override val linearId: UniqueIdentifier = UniqueIdentifier()

): LinearState