package com.template

import com.template.states.PreorderState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

class PreOrderContract : Contract {
    companion object {
        const val Contract_ID = "com.template.OrderContract"
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()
        when (command.value) {
            is Commands.PreOrder -> requireThat {
                "There must be only one Fungible Token" using (tx.outputs.size == 1)
                "No inputs should be consumed when pre-ordering" using (tx.inputs.isEmpty())
                val output = tx.outputStates.single() as PreorderState
                "The user and the platform are the only signers in a transaction"
                (command.signers.toSet() == output.participants.map { it.owningKey }.toSet())
                "The pre-order token must be greater than 0" using (output.amount > 0)


            }
        }
    }


    interface Commands : CommandData {
        class PreOrder : TypeOnlyCommandData(), Commands
    }
}