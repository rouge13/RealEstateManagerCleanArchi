package com.openclassrooms.realestatemanager

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.openclassrooms.realestatemanager.ui.alertDialog.LoanSimulatorAlertDialog
import org.robolectric.RuntimeEnvironment

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@RunWith(RobolectricTestRunner::class)
class LoanSimulatorMonthlyPaymentTest {
    @Test
    fun testMonthlyPayment() {
        val loanSimulatorAlertDialog = LoanSimulatorAlertDialog(RuntimeEnvironment.application)

        var monthlyPayment = loanSimulatorAlertDialog.calculateMonthlyPayment(LOAN_AMOUNT_1, INTEREST_RATE_1, LOAN_DURATION_1, CONTRIBUTION_1)
        // Assert that the result is as expected (replace expected with actual expected value)
        assertEquals(EXPECTED_1, monthlyPayment, DELTA)

        monthlyPayment = loanSimulatorAlertDialog.calculateMonthlyPayment(LOAN_AMOUNT_2, INTEREST_RATE_2, LOAN_DURATION_2, CONTRIBUTION_2)
        // Assert that the result is as expected (replace expected with actual expected value)
        assertEquals(EXPECTED_2, monthlyPayment, DELTA)
    }

    companion object {
        // For each test, replace the expected values with the actual expected values
        // First :
        private const val LOAN_AMOUNT_1 = 100000
        private const val INTEREST_RATE_1 = 5.0
        private const val LOAN_DURATION_1 = 30
        private const val CONTRIBUTION_1 = 20000

        // Second :
        private const val LOAN_AMOUNT_2 = 200000
        private const val INTEREST_RATE_2 = 4.5
        private const val LOAN_DURATION_2 = 20
        private const val CONTRIBUTION_2 = 50000

        // Expected :
        private const val EXPECTED_1 = 429.4572984097118
        private const val EXPECTED_2 = 948.974064329956

        // Delta value for the assertEquals method that is used when comparing double values for equality :
        // assertEquals(expected, actual, delta)
        // delta is the maximum delta between expected and actual for which both numbers are still considered equal.

        // With a delta of 0.01, 1.0 and 1.01 are considered equal.
        private const val DELTA = 0.01
    }
}