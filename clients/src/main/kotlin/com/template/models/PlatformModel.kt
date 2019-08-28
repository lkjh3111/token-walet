package com.template.webserver.models

import com.fasterxml.jackson.annotation.JsonCreator
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

data class PlatformModel (
        val amount: Long,
        val currency: String,
        val ownerId: String,
        val participants: List<Party>,
        val linearId: UniqueIdentifier

)

data class ExhangeRate (
        private val convert_from: String,
        private val to: String,
        private val ownerId: String
)

data class AcceptPreOrder @JsonCreator constructor(
        val approve_request: Boolean,
        val txId: String
)