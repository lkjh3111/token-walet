package com.template

import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

class RequestContract : Contract {
    companion object{
        const val Request_ID = "com.template.Request"
    }


    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()
        when    (command.value) {
            is Commands.RequestAccess -> requireThat {

            }


        is Commands.Accept -> requireThat {

        }
        }
    }

    interface Commands : CommandData{
        class RequestAccess : TypeOnlyCommandData(), Commands
        class Accept : TypeOnlyCommandData(), Commands
    }

}