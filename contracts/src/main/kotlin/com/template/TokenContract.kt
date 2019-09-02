package com.template

import com.template.states.TokenWalletState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction


class TokenContract() : Contract {
    companion object {
        const val ID = "com.template.TokenContract"
    }


    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()
        when (command.value) {
            is Commands.Register -> requireThat {
                "Registering should not consume registration" using (tx.inputs.isEmpty())
                "Only one output state should be created" using (tx.outputs.size == 1)
                val outputRegister = tx.outputsOfType<TokenWalletState>().single()
                val y = tx.outputStates
                val user = tx.outputStates.single() as TokenWalletState
                "Must be signed by the Registering node" using (command.signers.toSet() == outputRegister.participants.map { it.owningKey }.toSet())
                "Password must contain at least 6 characters with at least one Uppercase, at least one number" using (outputRegister.password.length >= 6)
                "Amount must be greater than 0" using (user.wallet.first().quantity > 0)
                "Amount must be greater than 0" using (user.wallet.last().quantity > 0)

            }
            is Commands.Exchange -> requireThat {
                val k = tx.outputStates.single() as TokenWalletState
                val r = tx.inputStates.single() as TokenWalletState
                val input = tx.groupStates<TokenWalletState, UniqueIdentifier> { it.linearId }.single()
                "There must be a transaction" using (input.inputs.size == 1)
                "When Exchanging it should create a transaction" using (tx.outputs.size == 1)
                "When Exchanging it should consume the transaction" using (tx.inputs.size == 1)
                "Owner is the only signer" using (command.signers.toSet() == k.participants.map { it.owningKey }.toSet())
                "The amount to convert must be greater than 0" using (r.wallet.first().quantity > 0)


            }
        }


    }
    interface Commands : CommandData {
        class Register : TypeOnlyCommandData(), Commands
        class Exchange : TypeOnlyCommandData(), Commands
        class Accept : TypeOnlyCommandData(), Commands
    }
}



