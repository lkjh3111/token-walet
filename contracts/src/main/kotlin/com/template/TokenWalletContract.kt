package com.template

import com.template.states.TokenWalletState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
class TokenWalletContract : Contract {
    companion object {
        const val ID = "com.template.contract.TokenWalletContract"
    }
    interface Commands : CommandData {
        class Preorder : TypeOnlyCommandData(), Commands
        class Register : TypeOnlyCommandData(), Commands
        class Issue : TypeOnlyCommandData(), Commands
        class Transfer : TypeOnlyCommandData(), Commands
        class Redeem : TypeOnlyCommandData(), Commands
    }
    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<TokenWalletContract.Commands>()
        when(command.value){
            is Commands.Preorder -> requireThat {
            }
            is Commands.Register ->   requireThat {
                "No inputs should be consumed when creating KYC." using (tx.inputs.isEmpty())
                "Only one output state should be created" using (tx.outputs.size == 1)
                "Output must be a KYCState" using (tx.getOutput(0) is TokenWalletState)
                val outputRegister = tx.outputsOfType<TokenWalletState>().single()
                "Must be signed by the Registering node" using (command.signers.toSet() == outputRegister.participants.map { it.owningKey }.toSet())
                "Username must not be empty" using (outputRegister.username.isNotEmpty())
                "Password must not be empty " using (outputRegister.password.isNotEmpty())
                "Participants must only be the registering node" using (outputRegister.participants.size == 1)
            }
            is Commands.Issue -> requireThat {
                //                val inputValidate = tx.inputsOfType<TokenWalletState>()
//                val outputValidate = tx.outputsOfType<TokenWalletState>()
//                "Only one input should be consumed when validating" using (inputValidate.size == 1)
//                "Only one output should be consumed when validating" using (outputValidate.size == 1)
//
//                "Input must be KYCState" using (tx.inputStates[0] is KYCState)
//                "Output must be KYCState" using (tx.outputStates[0] is KYCState)
//
//                "Input Verified must be false" using (!inputValidate.single().isVerified)
//                "Output Verified must be true" using (outputValidate.single().isVerified)
//                val inputValidateState = inputValidate.single()
//                val outputValidateState = inputValidate.single()
//                "Node inputState and outputState" using (inputValidateState.node == outputValidateState.node)
//                "Name inputState and outputState" using (inputValidateState.name == outputValidateState.name)
//                "Age inputState and outputState" using (inputValidateState.age == outputValidateState.age)
//                "Address inputState and outputState" using (inputValidateState.address == outputValidateState.address)
//                "Birthday inputState and outputState" using (inputValidateState.birthDate == outputValidateState.birthDate)
//                "Status inputState and outputState" using (inputValidateState.status == outputValidateState.status)
//                "Religion inputState and outputState" using (inputValidateState.religion == outputValidateState.religion)
//                "List inputState and outputState" using (inputValidateState.listOfParties == outputValidateState.listOfParties)
            }
            is Commands.Transfer -> requireThat {
                //                val inputUpdate = tx.inputsOfType<KYCState>()
//                val outputUpdate = tx.outputsOfType<KYCState>()
//                "Only one input should be consumed when updating" using (inputUpdate.size == 1)
//                "Only one output should be created when updating" using (outputUpdate.size == 1)
//                "Input must be KYCState" using (tx.inputStates[0] is KYCState)
//                "Output must be KYCState" using (tx.inputStates[0] is KYCState)
//                "Command must be Update" using (command.value is Commands.Update)
            }
            is Commands.Redeem -> requireThat {
                //                val inputSendReq = tx.inputsOfType<KYCState>()
//                val outputSendReq = tx.outRefsOfType<KYCState>()
//                "Only one input KYCState must be consumed" using (inputSendReq.size == 1)
//                "Only one output KYCState must be created" using (outputSendReq.size == 1)
//                "Command must be SendRequest" using (command.value is Commands.SendRequest)
//                val inputSendReqState = inputSendReq.single()
//                val outputSendReqState = inputSendReq.single()
//                // "OutputState should have new Node" using (inputSendReqState.node != outputSendReqState.node)
//                "Name inputState and outputState" using (inputSendReqState.name == outputSendReqState.name)
//                "Age inputState and outputState" using (inputSendReqState.age == outputSendReqState.age)
//                "Address inputState and outputState" using (inputSendReqState.address == outputSendReqState.address)
//                "Birthday inputState and outputState" using (inputSendReqState.birthDate == outputSendReqState.birthDate)
//                "Status inputState and outputState" using (inputSendReqState.status == outputSendReqState.status)
//                "Religion inputState and outputState" using (inputSendReqState.religion == outputSendReqState.religion)
//                "isVerified inputState and outputState" using (inputSendReqState.isVerified == outputSendReqState.isVerified)
////                val currentParticipantSize  = inputSendReqState.listOfParties.size //1
////                val newParticipantSize = outputSendReqState.listOfParties.size //2
////                "New one participant should be added" using ((currentParticipantSize+1) == newParticipantSize)
            }
        }
    }
}