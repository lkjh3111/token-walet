package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.template.states.TokenWalletState
import net.corda.core.contracts.Amount
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder


@InitiatingFlow
@StartableByRPC
class TokenRegisterFlow(
                private val username: String,
                private val password: String

               // private val amount: Long,
                //private val currency: String
) : Test() {

    @Suspendable
    override fun call():SignedTransaction {
        val wallet = mutableListOf(0 of TokenType(tokenIdentifier = "PHP", fractionDigits = 2),0 of TokenType(tokenIdentifier = "USD", fractionDigits = 2))
        //val token = FiatCurrency.getInstance(currency)
        print(wallet)
        val userState = TokenWalletState(username,password,null, wallet,listOf(ourIdentity))
        val tx: TransactionBuilder = transactions(userState)
        val signedTransaction: SignedTransaction = verifyAndSign(tx)
        val transactionSigned: SignedTransaction = collectSignatures(signedTransaction)
        return recordTransactions(transactionSigned)
    }
}
