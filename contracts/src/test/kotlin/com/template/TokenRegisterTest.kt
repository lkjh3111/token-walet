package com.template

import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.template.states.TokenWalletState
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test


class TokenRegisterTest {
    class Register : TypeOnlyCommandData()
    private val miniCorp = TestIdentity(CordaX500Name("MiniCorp", "London", "GB"))
    private val megaCorp = TestIdentity(CordaX500Name("MegaCorp", "London", "GB"))
    private var ledgerServices = MockServices(listOf("com.template.test"), megaCorp, miniCorp)


    @Test
    fun mustIncludeRegisterCommand() {
        val reg = TokenWalletState("Wia", "malagu", mutableListOf(123 of TokenType("PHP", 2)), listOf(megaCorp.party, miniCorp.party))
        ledgerServices.ledger {
          transaction {
              output(TokenContract.ID, reg)
              command(listOf(megaCorp.publicKey, miniCorp.publicKey), Register())
              this.`fails with`("")
          }
            transaction {
                output(TokenContract.ID, reg)
                command(listOf(),TokenContract.Commands.Register())
                this.verifies()
            }

        }
        }


    }



