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

// User -> Platform
@StartableByRPC
class TokenPreorder(private val amount: Long,
                    private val currency: String,
                    private val ownerId: String) : Test() {

    @Suspendable
    override fun call(): SignedTransaction {

        val token = FiatCurrency.getInstance(currency)
        val inputStateRef = inputStateRef(stringToLinearId(ownerId))
        val input = inputStateRef.state.data
//        val userState = TokenWalletState(input.username,input.username, input.wallet,listOf(ourIdentity))
        val tx: TransactionBuilder = transaction(input.copy(participants = input.participants),inputStateRef , Command(TokenWalletContract.Commands.Preorder(), listOf(ourIdentity.owningKey)))
        val signedTransaction: SignedTransaction = verifyAndSign(tx)
        return recordTransactionWithParty(signedTransaction)
    }
}

