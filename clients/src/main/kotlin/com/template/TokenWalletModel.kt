package com.template

import com.fasterxml.jackson.annotation.JsonCreator
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.Amount
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

data class TokenWalletModel (
        val username: String,
        val password: String,
        val wallet: MutableList<Amount<TokenType>>,
        val participants: List<Party>,
        val linearId: UniqueIdentifier
)

data class Register @JsonCreator constructor(
        val username: String,
        val password: String,
        val quantity: List<Long>,
        val currency: List<String>
)

data class PreOrder @JsonCreator constructor(
        val amount: Long,
        val currency: String,
        val ownerId: String
)

data class AcceptPreOrder @JsonCreator constructor(
        val approve_request: Boolean,
        val txId: String
)

data class SelfIssue @JsonCreator constructor(
        val amount: Long,
        val currency: String
)

data class OrderIssuer @JsonCreator constructor(
        val amount: Long,
        val currency: String
)

data class Transfer @JsonCreator constructor(
        val approve_request: Boolean,
        val txId: String
)

