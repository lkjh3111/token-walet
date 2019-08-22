package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.money.FiatCurrency
import com.template.TokenWalletContract
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

// User -> Platform
@StartableByRPC
class TokenPreOrder(private val amount: Long,
                    private val currency: String,
                    private val ownerId: String) : Test() {

    @Suspendable
    override fun call(): SignedTransaction {
        return recordTransactionsWithoutOtherParty(verifyAndSign())

        val token = FiatCurrency.getInstance(currency)
        val inputStateRef = inputStateRef(stringToLinearId(ownerId))
        val input = inputStateRef.state.data
        val tx: TransactionBuilder = transaction(input.copy(participants = input.participants),inputStateRef , Command(TokenWalletContract.Commands.Preorder(), listOf(ourIdentity.owningKey)))
        val signedTransaction: SignedTransaction = verifyAndSign(tx)
        return recordTransactionsWithoutOtherParty(signedTransaction)
    }
}

