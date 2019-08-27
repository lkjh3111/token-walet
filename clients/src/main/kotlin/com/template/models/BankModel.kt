package com.template.webserver.models

import com.fasterxml.jackson.annotation.JsonCreator

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