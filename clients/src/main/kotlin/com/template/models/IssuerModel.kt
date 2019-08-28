package com.template.models

import com.fasterxml.jackson.annotation.JsonCreator
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party


data class IssuerModel (
        val amount : Long,
        val currency: String,
        val approve_request: Boolean,
        val participants: List<Party>,
        val linearId: UniqueIdentifier

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