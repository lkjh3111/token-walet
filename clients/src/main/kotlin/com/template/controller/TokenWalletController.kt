package com.template.controller

import com.template.states.TokenWalletState
import com.template.webserver.NodeRPCConnection
import com.template.webserver.connection.NodeRPCConnection
import com.template.webserver.models.TokenWalletModel
import net.corda.core.messaging.vaultQueryBy
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val CONTROLLER_NAME = "config.controller.name"

@RestController
    @CrossOrigin(origins = ["*"])
    @RequestMapping("/") // The paths for HTTP requests are relative to this base path.
    class TokenWalletController(
            private val rpc: NodeRPCConnection
    ) {
        companion object {
            private val logger = LoggerFactory.getLogger(RestController::class.java)
        }

    /**
    TokenWallet ACCOUNTS
     **/

    private val proxy = rpc.proxy
    @GetMapping(value = ["/accounts"], produces = ["application/json"])
    private fun getAccounts(): ResponseEntity<Map<String, Any>> {
        val (status, result) = try {
            val stateRef = rpc.proxy.vaultQueryBy<TokenWalletState>().states
            val states = stateRef.map { it.state.data }
            val list = states.map {
                TokenWalletModel(
                        username = it.username,
                        password = it.password,
                        wallet = it.wallet.toString()
                )
            }
            HttpStatus.CREATED to list
        } catch (e: Exception) {
            HttpStatus.BAD_REQUEST to "No data"
        }
        val stat = "status" to status
        val mess = if (status == HttpStatus.CREATED) {
            "message" to "Success in getting Account Information"
        } else {
            "message" to "Failed to get Account Information"
        }
        val res = "result" to result
        return ResponseEntity.status(status).body(mapOf(stat, mess, res))
    }






}


