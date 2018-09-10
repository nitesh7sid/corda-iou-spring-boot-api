package com.hmlr.api.controllers

import com.example.flow.ExampleFlow
import com.example.state.IOUState
import com.hmlr.api.rpcClient.NodeRPCConnection
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.node.services.IdentityService
import net.corda.core.utilities.getOrThrow
import net.corda.core.utilities.loggerFor
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


/**
 * @author Nitesh Solanki
 */

@RestController
@RequestMapping("/api")
class ApiController(private val rpc: NodeRPCConnection) {

    private val rpcOps = rpc.proxy
    val myIdentity = rpcOps.nodeInfo().legalIdentities.first()

    companion object {
        private val logger = loggerFor<ApiController>()
    }

    /**
     * Return the node's name
     */
    @GetMapping(value = "/me", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun me() = mapOf("me" to myIdentity.name)

    /**
     * Returns all parties registered with the [NetworkMapService]. These names can be used to look up identities
     * using the [IdentityService].
     */
    @GetMapping(value = "/peers", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun peers() = mapOf("peers" to rpcOps.networkMapSnapshot()
            .filter { nodeInfo -> nodeInfo.legalIdentities.first() != myIdentity }
            .map { it.legalIdentities.first().name.organisation })

    /**
     * Initiates a flow to agree an IOU between two parties.
     *
     * Once the flow finishes it will have written the IOU to ledger. Both the lender and the borrower will be able to
     * see it when calling /api/ious on their respective nodes.
     *
     * This end-point takes a Party name parameter as part of the path. If the serving node can't find the other party
     * in its network map cache, it will return an HTTP bad request.
     *
     * The flow is invoked asynchronously. It returns a future when the flow's call() method returns.
     */
    @PutMapping(value = "/create-iou", produces = arrayOf(MediaType.TEXT_PLAIN_VALUE))
    fun createIOU(@RequestParam("iouValue") iouValue: Int, @RequestParam("partyName") partyName: String): ResponseEntity<Any?> {
        if (iouValue <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Query parameter 'iouValue' must be non-negative.\n")
        }
        val otherParty = rpcOps.partiesFromName(partyName, false).first()

        val (status, message) = try {
            val flowHandle = rpcOps.startFlowDynamic(ExampleFlow.Initiator::class.java, iouValue, otherParty)
            val result = flowHandle.use { it.returnValue.getOrThrow() }
            HttpStatus.CREATED to "Transaction id ${result.tx.id} committed to ledger.\n${result.tx.outputs.single().data}"
        } catch (e: Exception) {
            HttpStatus.BAD_REQUEST to e.message
        }
        logger.info(message)
        return ResponseEntity<Any?>(message, status)
    }

    /**
     * Displays all IOU states that exist in the node's vault.
     */
    @GetMapping(value = "/ious", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun getIOUs() = rpcOps.vaultQueryBy<IOUState>().states

}