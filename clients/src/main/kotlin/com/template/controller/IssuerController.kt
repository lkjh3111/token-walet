package com.template.controller

import com.template.flows.SelfIssueTokenFlow
import com.template.flows.TokenOrderIssuerFlow
import com.template.flows.TokenTransferFlow
import com.template.states.IssueOrderState
import com.template.states.TokenWalletState
import com.template.webserver.connection.NodeRPCConnection
import com.template.webserver.models.*
import net.corda.core.messaging.vaultQueryBy
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class IssuerController(
        private val rpc: NodeRPCConnection
) {
    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    /**
    Issuer ACCOUNTS
     **/

    private val proxy = rpc.proxy
    @GetMapping(value = ["/issueraccounts"], produces = ["application/json"])
    private fun getAccounts(): ResponseEntity<Map<String, Any>> {
        val (status, result) = try {
            val stateRef = rpc.proxy.vaultQueryBy<IssueOrderState>().states
            val states = stateRef.map { it.state.data }
            val list = states.map {
                IssuerModel(
                        amount = it.amount,
                        currency = it.currency,
                        approve_request = it.approve_request,
                        participants = it. participants,
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
    TokenWallet SELF-ISSUE
     **/


    @PostMapping(value = ["/selfissue"], produces = ["application/json"])
    private fun selfIssue(@RequestBody selfIssue: SelfIssue): ResponseEntity<Map<String, Any>> {
        val (status, result) = try {
            val slf = SelfIssue(

                    amount = selfIssue.amount,
                    currency = selfIssue.currency

            )
            proxy.startFlowDynamic(
                    SelfIssueTokenFlow::class.java,
                    slf.amount,
                    slf.currency
            )
            HttpStatus.CREATED to "Success"
        } catch (e: Exception) {
            HttpStatus.BAD_REQUEST to "No data"
        }
        val stat = "status" to status.value()
        val mess = if (status == HttpStatus.CREATED) {
            "message" to "Success in Self-Issue of Tokens."
        } else {
            "message" to "Failed in Self-Issue of Tokens."
        }
        val res = "result" to result
        return ResponseEntity.status(status).body(mapOf(stat, mess, res))
    }

    /**
    TokenWallet ORDER-ISSUER
     **/
    @PostMapping(value = ["/orderissuer"], produces = ["application/json"])
    private fun orderIssuer(@RequestBody orderIssuer: OrderIssuer): ResponseEntity<Map<String, Any>> {
        val (status, result) = try {
            val odi = OrderIssuer(

                    amount = orderIssuer.amount,
                    currency = orderIssuer.currency

            )
            proxy.startFlowDynamic(
                    TokenOrderIssuerFlow::class.java,
                    odi.amount,
                    odi.currency
            )
            HttpStatus.CREATED to "Success"
        } catch (e: Exception) {
            HttpStatus.BAD_REQUEST to "No data"
        }
        val stat = "status" to status.value()
        val mess = if (status == HttpStatus.CREATED) {
            "message" to "Success in Order to tokens to Platform."
        } else {
            "message" to "Failed in Order to tokens to Platform."
        }
        val res = "result" to result
        return ResponseEntity.status(status).body(mapOf(stat, mess, res))
    }

    /**
    TokenWallet TRANSFER
     **/

    @PostMapping(value = ["/transfer"], produces = ["application/json"])
    private fun transfer(@RequestBody transfer: Transfer): ResponseEntity<Map<String, Any>> {
        val (status, result) = try {
            val trf = Transfer(

                    approve_request = transfer.approve_request,
                    txId = transfer.txId

            )
            proxy.startFlowDynamic(
                    TokenTransferFlow::class.java,
                    trf.approve_request,
                    trf.txId
            )
            HttpStatus.CREATED to "Success"
        } catch (e: Exception) {
            HttpStatus.BAD_REQUEST to "No data"
        }
        val stat = "status" to status.value()
        val mess = if (status == HttpStatus.CREATED) {
            "message" to "Success in transfer of token to User."
        } else {
            "message" to "Failed in transfer of token to User."
        }
        val res = "result" to result
        return ResponseEntity.status(status).body(mapOf(stat, mess, res))
    }
}