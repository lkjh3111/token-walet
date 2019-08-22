package com.template

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract
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

            }

            is Commands.Preorder -> requireThat{

            }

            is Commands.Transfer -> requireThat {

            }

            is Commands.Issue -> requireThat {

            }
            is Commands.Move -> requireThat {

            }

        }

    }

}