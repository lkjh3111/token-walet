package com.template.controller

import com.template.flows.TokenPreOrderFlow
import com.template.flows.TokenRegisterFlow
import com.template.states.TokenWalletState
import com.template.webserver.connection.NodeRPCConnection
import com.template.webserver.models.PreOrder
import com.template.webserver.models.Register
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
                        wallet = it.wallet.toString(),
                        linearId = it.linearId
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

    /**
    TokenWallet REGISTER
     **/

    @PostMapping(value = ["/register"], produces = ["application/json"])
    private fun register(@RequestBody register: Register): ResponseEntity<Map<String, Any>> {
        val (status, result) = try {
            val reg = Register(
                    username = register.username,
                    password = register.password,
                    quantity = register.quantity,
                    currency = register.currency
            )
            proxy.startFlowDynamic(
                    TokenRegisterFlow::class.java,
                    reg.username,
                    reg.password,
                    reg.quantity,
                    reg.currency
            )
            HttpStatus.CREATED to "Success"
        } catch (e: Exception) {
            HttpStatus.BAD_REQUEST to "No data"
        }
        val stat = "status" to status.value()
        val mess = if (status == HttpStatus.CREATED) {
            "message" to "Successful in Registering User to Platform."
        } else {
            "message" to "Failed to Register User to Platform."
        }
        val res = "result" to result
        return ResponseEntity.status(status).body(mapOf(stat, mess, res))
    }

    /**
    TokenWallet PRE-ORDER
     **/

    @PostMapping(value = ["/preorder"], produces = ["application/json"])
    private fun preOrder(@RequestBody preOrder: PreOrder): ResponseEntity<Map<String, Any>> {
        val (status, result) = try {
            val pre = PreOrder(
                    amount = preOrder.amount,
                    currency = preOrder.currency,
                    ownerId = preOrder.ownerId

            )
            proxy.startFlowDynamic(
                    TokenPreOrderFlow::class.java,
                    pre.amount,
                    pre.currency,
                    pre.ownerId
            )
            HttpStatus.CREATED to "Success"
        } catch (e: Exception) {
            HttpStatus.BAD_REQUEST to "No data"
        }
        val stat = "status" to status.value()
        val mess = if (status == HttpStatus.CREATED) {
            "message" to "Successful in Pre-Order of token to Platform."
        } else {
            "message" to "Failed to Pre-Order token to Platform."
        }
        val res = "result" to result
        return ResponseEntity.status(status).body(mapOf(stat, mess, res))
    }
}