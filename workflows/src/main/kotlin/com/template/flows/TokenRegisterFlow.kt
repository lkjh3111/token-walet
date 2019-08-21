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
import org.omg.CORBA.ARG_IN.value


@InitiatingFlow
@StartableByRPC
class TokenRegisterFlow(
                private val username: String,
                private val password: String,
                private val wallet: MutableList<Amount<TokenType>>,
                private val quantity: List<Long>,
                private val currency: List<String>,
                private val digits: List<Long>


               // private val amount: Long,
                //private val currency: String
) : Test() {

    @Suspendable
    override fun call():SignedTransaction {
//        val wallet = mutableListOf(0 of TokenType(tokenIdentifier = "PHP", fractionDigits = 2),0 of TokenType(tokenIdentifier = "USD", fractionDigits = 2))

//        val token = FiatCurrency.getInstance(currency)
        val wallet = mutableListOf<Amount<TokenType>>()

        for ((index  , value ) in quantity.withIndex()){
            val token = FiatCurrency.getInstance(currency[index])
            wallet.plus(value of token)
        }







        val userState = TokenWalletState(username,password,null, wallet,listOf(ourIdentity))
        val tx: TransactionBuilder = transactions(userState)
        val signedTransaction: SignedTransaction = verifyAndSign(tx)
        val transactionSigned: SignedTransaction = collectSignatures(signedTransaction)
        return recordTransactions(transactionSigned)
    }
}
