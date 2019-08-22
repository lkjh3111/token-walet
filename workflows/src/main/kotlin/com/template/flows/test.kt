package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.TokenWalletContract
import com.template.TokenWalletContract.Companion.ID
import com.template.states.TokenWalletState
import net.corda.core.contracts.*
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

abstract class Test: FlowLogic<SignedTransaction>() {

    fun stringToParty(user2: String): Party {
        return serviceHub.identityService.partiesFromName(user2, false).singleOrNull()
                ?: throw IllegalArgumentException("No match found for $user2")

    }

    fun inputStateRef(id: UniqueIdentifier): StateAndRef<TokenWalletState> {
        val criteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(id))
        return serviceHub.vaultService.queryBy<TokenWalletState>(criteria = criteria).states.single()
    }

    fun verifyAndSign(tx: TransactionBuilder): SignedTransaction {
        tx.verify(serviceHub)
        return serviceHub.signInitialTransaction(tx)
    }

    fun transactions(spiedOnMessage: ContractState): TransactionBuilder {
            val notary = serviceHub.networkMapCache.notaryIdentities.first()
            val txCommand = Command(TokenWalletContract.Commands.Register(),ourIdentity.owningKey)
            return TransactionBuilder(notary)
                    .addOutputState(spiedOnMessage, ID)
                    .addCommand(txCommand)
    }

    fun transaction(spiedOnMessage: ContractState,input: StateAndRef<ContractState>, txCommand: Command<CommandData>): TransactionBuilder {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        return TransactionBuilder(notary)
                .addInputState(input)
                .addOutputState(spiedOnMessage, ID)
                .addCommand(txCommand)
    }

    @Suspendable
    fun collectSignature(partySignedTx: SignedTransaction, sessions: List<FlowSession>): SignedTransaction
            = subFlow(CollectSignaturesFlow(partySignedTx, sessions))

    @Suspendable
    fun recordTransactionsWithoutOtherParty(partySignedTx: SignedTransaction):
            SignedTransaction = subFlow(FinalityFlow(partySignedTx, listOf()))

    @Suspendable
    fun recordTransactionWithParty(partySignedTx: SignedTransaction, sessions: FlowSession):
            SignedTransaction = subFlow(FinalityFlow(partySignedTx, sessions))

    fun stringToLinearId(string: String): UniqueIdentifier {
        return UniqueIdentifier.fromString(string)
    }
}