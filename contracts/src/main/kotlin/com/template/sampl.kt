package com.template



import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.of
import net.corda.core.contracts.Amount
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker
import org.jetbrains.annotations.NotNull
import java.math.BigDecimal


//class MyFixedToken(@get:NotNull
//                   override  val tokenIdentifier: String, override val fractionDigits: Int) : TokenType(tokenIdentifier, fractionDigits) {
//
//    override  val tokenClass: Class<*>
//        @NotNull
//        get() = this.javaClass
//
//    override val displayTokenSize: BigDecimal
//        @NotNull
//        get() = BigDecimal.ONE.scaleByPowerOfTen(-fractionDigits)
//
//    override fun equals(obj: Any?): Boolean {
//        var other: MyFixedToken? = null
//        if (obj is MyFixedToken) {
//            other = obj
//            if (this.tokenIdentifier == other.tokenIdentifier)
//                return true
//        }
//        return false
//    }
//}


/**
 * v2
 */


//class IssueMyFixedToken(val tokenIdentifier: String, val amount: Long, val recipient: Party) : FlowLogic<SignedTransaction>() {
//    override val progressTracker = ProgressTracker()
//    @Suspendable
//    override fun call(): SignedTransaction {
//        val token = MyFixedToken(tokenIdentifier)
//            val tokenType = IssuedTokenType(ourIdentity,token)
//           val fungibleToken = FungibleToken(Amount(amount,tokenType),recipient, TransactionUtilitiesKt.getAttachmentIdForGenericParam(token))
//         Starts a new flow session.
//        return subFlow(IssueTokens(listOf(amount of token issuedBy ourIdentity heldBy recipient)))
//    }
//}
//
