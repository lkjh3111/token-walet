package com.template

import com.template.states.IssueOrderState
import net.corda.core.contracts.*
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction

class IssueOrderContract : Contract {
    companion object {
        const val IssueOrder_ID = "com.template.IssueOrder"
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()
        when (command.value) {
            is Commands.Issue -> requireThat {
                "Issuing order should not consume the transaction" using(tx.inputs.isEmpty())
                "There must be one order transaction" using(tx.outputs.size==1)
                val order = tx.outputStates.single() as IssueOrderState
                "The Ordered Token must be greater than " using(order.amount>0)
            }
            is Commands.Transfer -> {
                "A transaction should only consume one input state." using (tx.inputs.size == 1)
                val output = tx.outputStates.single() as IssueOrderState
                "An  transaction should only create one output state." using (tx.outputs.size == 1)
            }
        }
    }

    interface Commands: CommandData{
        class Issue: TypeOnlyCommandData(), Commands
        class Transfer: TypeOnlyCommandData(), Commands
    }
}