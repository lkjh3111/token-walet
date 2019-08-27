package com.template.webserver.models

import com.fasterxml.jackson.annotation.JsonCreator

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

