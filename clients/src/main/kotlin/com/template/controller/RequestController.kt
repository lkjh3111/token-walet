package com.template.controller

import com.template.flows.AcceptPreOrderFlow
import com.template.flows.RequestAccessFlow
import com.template.states.RequestState
import com.template.webserver.connection.NodeRPCConnection
import com.template.webserver.models.AcceptPreOrder
import com.template.webserver.models.Request
import com.template.webserver.models.RequestModel
import net.corda.core.messaging.vaultQueryBy
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val CONTROLLER_NAME = "config.controller.name"

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class RequestController(
        private val rpc: NodeRPCConnection
) {
    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }


    /**
    TokenWallet REQUEST LIST
     **/

    private val proxy = rpc.proxy

    @GetMapping(value = ["/requestlist"], produces = ["application/json"])
    private fun getRequestList(): ResponseEntity<Map<String, Any>> {
        val (status, result) = try {
            val stateRef = rpc.proxy.vaultQueryBy<RequestState>().states
            val states = stateRef.map { it.state.data }
            val list = states.map {
                RequestModel(
                        requestAccessTo = it.requestAccessTo.toString(),
                        requestStatus = it.requestStatus,
                        participants = it.participants.toString(),
                        linearId = it.linearId.toString()
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
    TokenWallet REQUEST
     **/

    @PostMapping(value = ["/request"], produces = ["application/json"])
    private fun request(@RequestBody request: Request): ResponseEntity<Map<String, Any>> {
        val (status, result) = try {
            val req = Request(

                    requestAccessTo = request.requestAccessTo


            )
            proxy.startFlowDynamic(
                    RequestAccessFlow::class.java,
                    req.requestAccessTo

            )
            HttpStatus.CREATED to "Success"
        } catch (e: Exception) {
            HttpStatus.BAD_REQUEST to "No data"
        }
        val stat = "status" to status.value()
        val mess = if (status == HttpStatus.CREATED) {
            "message" to "Approved Request Access"
        } else {
            "message" to "Rejected Request Access"
        }
        val res = "result" to result
        return ResponseEntity.status(status).body(mapOf(stat, mess, res))
    }
}