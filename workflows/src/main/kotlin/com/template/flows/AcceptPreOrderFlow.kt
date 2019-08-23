package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.money.FiatCurrency
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveFungibleTokens
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import com.template.TokenWalletContract
import com.template.states.PreorderState
import com.template.states.TokenWalletState
import net.corda.core.contracts.Amount
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@StartableByRPC
class AcceptPreOrderFlow(private val approve_request: Boolean,
                         private val txId: String) : Test()
{
    @Suspendable
    override fun call(): SignedTransaction
    {
        val inputStateRef = inputStateAndRef (stringToLinearId(txId))
        val input = inputStateRef.state.data
        val inputStateRefe = inputStateRefe(stringToLinearId(input.ownerId))
        val input1 = inputStateRefe.state.data

        val token = FiatCurrency.getInstance(input.currency)
        val search = input1.wallet.filter {input1.wallet.any{it == token}}
        print(search)
        if (approve_request){
            subFlow(MoveFungibleTokens(input.amount of token,  ourIdentity))
        }
        return recordTransactionsWithoutOtherParty(verifyAndSign(accept()))
    }

    private fun inputStateAndRef(id: UniqueIdentifier): StateAndRef<PreorderState> {
        val criteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(id))
        return serviceHub.vaultService.queryBy<PreorderState>(criteria = criteria).states.single()
    }

    private fun inputStateRefe(id: UniqueIdentifier): StateAndRef<TokenWalletState> {
        val criteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(id))
        return serviceHub.vaultService.queryBy<TokenWalletState>(criteria = criteria).states.single()
    }

    private fun outState(): PreorderState
    {
        val inputStateRef = inputStateAndRef (stringToLinearId(txId))
        val input = inputStateRef.state.data
        return PreorderState(
                input.amount,
                input.currency,
                approve_request,
                input.ownerId,
                listOf(ourIdentity)
        )
    }
    private fun outState2(): TokenWalletState
    {
        val inputStateRef = inputStateAndRef (stringToLinearId(txId))
        val input = inputStateRef.state.data
        val inputStateRefe = inputStateRefe(stringToLinearId(input.ownerId))
        val input1 = inputStateRefe.state.data
        return TokenWalletState(
               input1.username,
                input1.password,
                input1.wallet,
                input1.participants
        )
    }
    private fun accept() = TransactionBuilder(notary = getPreferredNotary(serviceHub)).apply {
        val participants = if (approve_request){
            listOf()
        }else {
            listOf(ourIdentity)
        }
        val inputStateRef = inputStateAndRef (stringToLinearId(txId))
        val input = inputStateRef.state.data
        val inputStateRefe = inputStateRefe(stringToLinearId(input.ownerId))
        val input1 = inputStateRefe.state.data
        val cmd = Command(TokenWalletContract.Commands.Accept(), listOf(ourIdentity.owningKey))
        addInputState(inputStateAndRef (stringToLinearId(txId)))
        addInputState(inputStateRefe(stringToLinearId(input.ownerId)))
        addOutputState(outState().copy(participants = participants))
        addOutputState(outState2())
        addCommand(cmd)
    }


}
