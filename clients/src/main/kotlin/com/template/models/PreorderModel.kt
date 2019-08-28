package com.template.webserver.models

import com.fasterxml.jackson.annotation.JsonCreator
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

data class PreorderModel (
        val amount: Long,
        val currency: String,
        val ownerId: String,
        val participants: String,
        val linearId: UniqueIdentifier
)

data class AcceptPreOrder @JsonCreator constructor(
        val approve_request: Boolean,
        val txId: String
)

data class ExchangeRate (
        val convert_from: String,
        val to: String,
        val ownerId: String
)