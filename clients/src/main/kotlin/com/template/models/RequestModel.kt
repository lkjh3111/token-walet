package com.template.models

import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

data class RequestModel (
        val requestAccessTo: String,
        val requestStatus: Boolean,
        val participants: String,
        val linearId: String
)

data class Request (
        val requestAccessTo: String
)