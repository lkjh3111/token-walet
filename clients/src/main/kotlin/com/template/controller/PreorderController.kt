package com.template.webserver.controller

import com.template.flows.AcceptPreOrderFlow
import com.template.flows.ExchangeRateFlow
import com.template.flows.TokenPreOrderFlow
import com.template.states.IssueOrderState
import com.template.states.PreorderState
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
class PreorderController(
        private val rpc: NodeRPCConnection
) {
    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    /**
    Pre-Order ACCOUNTS
     **/

    private val proxy = rpc.proxy
    @GetMapping(value = ["/getpreorder"], produces = ["application/json"])
    private fun getPreorder(): ResponseEntity<Map<String, Any>> {
        val (status, result) = try {
            val stateRef = rpc.proxy.vaultQueryBy<PreorderState>().states
            val states = stateRef.map { it.state.data }
            val list = states.map {
                PreorderModel(
                        amount = it.amount,
                        currency = it.currency,
                        ownerId = it.ownerId,
                        participants = it. participants.toString(),
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
    TokenWallet ACCEPT PRE-ORDER
     **/

    @PostMapping(value = ["/acceptpreorder"], produces = ["application/json"])
    private fun acceptPreOrder(@RequestBody acceptPreOrder: AcceptPreOrder): ResponseEntity<Map<String, Any>> {
        val (status, result) = try {
            val apo = AcceptPreOrder(

                    approve_request = acceptPreOrder.approve_request,
                    txId = acceptPreOrder.txId

            )
            proxy.startFlowDynamic(
                    AcceptPreOrderFlow::class.java,
                    apo.approve_request,
                    apo.txId
            )
            HttpStatus.CREATED to "Success"
        } catch (e: Exception) {
            HttpStatus.BAD_REQUEST to "No data"
        }
        val stat = "status" to status.value()
        val mess = if (status == HttpStatus.CREATED) {
            "message" to "Approved Pre-Order request to Platform."
        } else {
            "message" to "Rejected Pre-Order request to Platform."
        }
        val res = "result" to result
        return ResponseEntity.status(status).body(mapOf(stat, mess, res))
    }

    /**
    TokenWallet EXCHANGE RATE
     **/

    @PostMapping(value = ["/exchangerate"], produces = ["application/json"])
    private fun exchangeRate(@RequestBody exchangeRate: ExchangeRate): ResponseEntity<Map<String, Any>> {
        val (status, result) = try {
            val exr = ExchangeRate(

                    convert_from = exchangeRate.convert_from,
                    to = exchangeRate.to,
                    ownerId = exchangeRate.ownerId

            )
            proxy.startFlowDynamic(
                    ExchangeRateFlow::class.java,
                    exr.convert_from,
                    exr.to,
                    exr.ownerId
            )
            HttpStatus.CREATED to "Success"
        } catch (e: Exception) {
            HttpStatus.BAD_REQUEST to "No data"
        }
        val stat = "status" to status.value()
        val mess = if (status == HttpStatus.CREATED) {
            "message" to "Successfully Exchanged Rate of Currency."
        } else {
            "message" to "Failed to Exchange Rate of Currency."
        }
        val res = "result" to result
        return ResponseEntity.status(status).body(mapOf(stat, mess, res))
    }
}