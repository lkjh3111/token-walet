package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.money.FiatCurrency
import com.template.states.TokenWalletState
import jdk.nashorn.internal.parser.Token
import net.corda.core.contracts.Amount
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder


@InitiatingFlow
@StartableByRPC
class TokenRegisterFlow(
                private val username: String,
                private val password: String,
                private val quantity: List<Long>,
                private val currency: List<String>
                //private val fractionDigits: List<Int>

               // private val amount: Long,
                //private val currency: String
) : Test() {

    @Suspendable
    override fun call():SignedTransaction {
        val wallet = mutableListOf<Amount<TokenType>>()
        addtolist(wallet,quantity,currency)
        val userState = TokenWalletState(username,password,wallet,listOf(ourIdentity))
        val tx: TransactionBuilder = transactions(userState)
        val signedTransaction: SignedTransaction = verifyAndSign(tx)
        val transactionSigned: SignedTransaction = collectSignatures(signedTransaction)
        return recordTransactions(transactionSigned)
    }
}

private fun addtolist (wallet: MutableList<Amount<TokenType>>, quantity: List<Long>, currency: List<String>){
    for ((index, value) in quantity.withIndex()) {
        val token = FiatCurrency.getInstance(currency[index])
        wallet.add(index,value of token)
    }
}
