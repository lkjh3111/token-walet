package com.template

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract
import net.corda.core.contracts.Contract
import net.corda.core.transactions.LedgerTransaction

class TokenWalletContract : EvolvableTokenContract(), Contract {

    override fun additionalCreateChecks(tx: LedgerTransaction) {
        }


    override fun additionalUpdateChecks(tx: LedgerTransaction) {

    }
}