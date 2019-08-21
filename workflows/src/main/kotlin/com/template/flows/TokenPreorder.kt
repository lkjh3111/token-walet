package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.money.FiatCurrency
import com.template.TokenWalletContract
import com.template.TokenWalletContract.Companion.ID
import com.template.states.TokenWalletState
import jdk.nashorn.internal.parser.TokenType
import net.corda.core.contracts.Amount
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class TokenPreorder(private val amount: Long,
                    private val currency: String,
                    //private val issuer: Party,
                    private val linearId: UniqueIdentifier) : Test() {

    @Suspendable
    override fun call():SignedTransaction {
        val issuer = when (currency) {
            "PHP"->stringToParty("PartyB")
            "USD"->stringToParty("PartyC")
            else->null
        }
        print(issuer)
        val token = FiatCurrency.getInstance(currency)
        val inputStateRef = inputStateRef(linearId)
        val input = inputStateRef.state.data
        val userState = TokenWalletState(input.username,input.username, input.wallet,listOf(ourIdentity,issuer!!))
        val tx: TransactionBuilder = transaction(userState.copy(participants = userState.participants),inputStateRef , Command(TokenWalletContract.Commands.Preorder(), listOf(ourIdentity.owningKey,issuer.owningKey)))
        val signedTransaction: SignedTransaction = verifyAndSign(tx)
        val session = initiateFlow(issuer)
        val transactionSigned: SignedTransaction = collectSignature(signedTransaction,session)
        return recordTransaction(transactionSigned, session)
    }
}


@InitiatedBy(TokenPreorder::class)
class TokenPreorderResponder(val flowSession: FlowSession): FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a transaction" using (output is TokenWalletState)
            }
        }
        val txWeJustSignedId = subFlow(signedTransactionFlow)

        return subFlow(ReceiveFinalityFlow(otherSideSession = flowSession, expectedTxId = txWeJustSignedId.id))
    }
}