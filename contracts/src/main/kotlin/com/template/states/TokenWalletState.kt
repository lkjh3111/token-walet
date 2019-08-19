package com.template.states

import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType
import com.template.TokenWalletContract
import jdk.nashorn.internal.parser.TokenType
import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

@BelongsToContract(TokenWalletContract::class)
data class TokenWalletState(
        val username: String,
        val password: Long,
        val preorder_amount: Long,
        val currency: String,
        val PHP_wallet: Amount<TokenType>,
        val USD_wallet: Amount<TokenType>,
        override val maintainers: List<Party>,
        override val fractionDigits: Int = 0,
        override val linearId: UniqueIdentifier
) : EvolvableTokenType()