package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.heldBy
import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
import com.r3.corda.lib.tokens.money.FiatCurrency
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens
import com.template.TokenWalletContract
import com.template.states.TokenWalletState
import net.corda.core.contracts.Amount
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@StartableByRPC
class TokenIssueOrderFlow(private val linearId: UniqueIdentifier,
                          private val approve_request: Boolean) : Test() {


    @Suspendable
    override fun call(): SignedTransaction {

        //val token = FiatCurrency.getInstance(currency)
        val inputStateRef = inputStateRef(linearId)
        val input = inputStateRef.state.data
        val userState = TokenWalletState(input.username,input.password,input.requested_amount, input.wallet, input.participants)
        val participants = if (approve_request){
            userState.participants
        }else{
            listOf(stringToParty("PartyA"))
        }

        val tx: TransactionBuilder = transaction(userState.copy(participants = participants),inputStateRef, Command(TokenWalletContract.Commands.Transfer(), listOf(ourIdentity.owningKey,stringToParty("PartyA").owningKey)))
        val signedTransaction: SignedTransaction = verifyAndSign(tx)
        val session = initiateFlow(stringToParty("PartyA"))
        val transactionSigned: SignedTransaction = collectSignature(signedTransaction,session)
        if(approve_request){
            subFlow(IssueTokens(listOf(input.requested_amount!! issuedBy ourIdentity heldBy stringToParty("PartyA"))))
        }
        return recordTransaction(transactionSigned, session)
//        subFlow(IssueTokens(listOf(input.requested_amount!! issuedBy ourIdentity heldBy stringToParty("PartyA"))))

    }
}