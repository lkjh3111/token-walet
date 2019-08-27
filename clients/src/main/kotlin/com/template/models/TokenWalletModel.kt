    package com.template.webserver.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.Amount
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

data class TokenWalletModel (
        val username: String,
        val password: String,
        val wallet: String
)

//data class ResponseModel (
//        var status: String?,
//        var message: String?,
//        var result: Any?
//):java.io.Serializable








