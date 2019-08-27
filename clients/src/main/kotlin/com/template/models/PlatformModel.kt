package com.template.webserver.models

import com.fasterxml.jackson.annotation.JsonCreator



data class AcceptPreOrder @JsonCreator constructor(
        val approve_request: Boolean,
        val txId: String
)