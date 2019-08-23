package com.template

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract
import com.template.states.IssueOrderState
import com.template.states.TokenWalletState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

class TokenWalletContract: Contract {
    companion object{
        const val ID = "com.template.TokenWalletContract"
    }

    interface Commands : CommandData {
        class Register : TypeOnlyCommandData(), Commands
        class Preorder : TypeOnlyCommandData(), Commands
        class Issue : TypeOnlyCommandData(), Commands
        class Transfer : TypeOnlyCommandData(), Commands
        class Move : TypeOnlyCommandData(), Commands
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()

        when (command.value){
            is Commands.Register -> requireThat {
                "Registering should not consume registration" using (tx.inputs.isEmpty())
                "Only one output state should be created" using (tx.outputs.size == 1)
                val outputRegister = tx.outputsOfType<TokenWalletState>().single()
                val user = tx.outputStates.single() as TokenWalletState
                "Must be signed by the Registering node" using (command.signers.toSet() == outputRegister.participants.map { it.owningKey }.toSet())
                "Password must contain at least 6 characters with at least one Uppercase, at least one number" using (outputRegister.password.length>=6)
                "Amount must be greater than 0" using (user.wallet.first().quantity>0)
                "Amount must be greater than 0" using (user.wallet.last().quantity>0)
//                "Password must contain at least 6 characters with with at least one Uppercase, at least one number" using (outputRegister.password.)
        }

            is Commands.Preorder -> requireThat{


            }

            is Commands.Transfer -> requireThat {

//
                "A transaction should only consume one input state." using (tx.inputs.size == 1)
//                "An  transaction should only create one output state." using (tx.outputs.size == 1)
//                val input = tx.inputStates
//                val output = tx.outputStates.single() as TokenWalletState
//                "Only the lender property may change." using (input == output.withNewLender(input.lender))
//                "The lender property must change in a transfer." using (input.lender != output.lender)
//                "The Issuer and Platform only must sign a transaction" using
//                        (command.signers.toSet() == (input.participants.map { it.owningKey }.toSet() `union`
//                                output.participants.map { it.owningKey }.toSet()))

            }

            is Commands.Issue -> requireThat {

            }
            is Commands.Move -> requireThat {

            }

        }

    }

}