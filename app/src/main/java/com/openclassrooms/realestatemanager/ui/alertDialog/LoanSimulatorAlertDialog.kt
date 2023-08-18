package com.openclassrooms.realestatemanager.ui.alertDialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.DialogRealEstateLoanSimulatorBinding
import java.text.DecimalFormat
import kotlin.math.pow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class LoanSimulatorAlertDialog(private val context: Context) {

    private var alertDialog: AlertDialog? = null
    private lateinit var binding: DialogRealEstateLoanSimulatorBinding

    fun showLoanSimulator(priceOfProperty : Int, isEuroOrDollarSelected: Boolean) {
        // Create the AlertDialog builder and inflate the layout for the dialog and set up the view for the dialog and the cancel button
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.real_estate_loan_simulator)
        val inflater = LayoutInflater.from(context)
        binding = DialogRealEstateLoanSimulatorBinding.inflate(inflater)
        builder.setView(binding.root)
        // Add the cancel button
        builder.setNegativeButton(R.string.exit) { _, _ ->
            alertDialog?.dismiss()
        }
        alertDialog = builder.create()
        alertDialog?.show()
        setupListeners(priceOfProperty, isEuroOrDollarSelected)
    }

    // Calculate the monthly payment for a loan by getting the loan amount, the interest rate, the loan duration and the personal contribution and return the monthly payment with the listener
    private fun setupListeners(priceOfProperty: Int, isEuroOrDollarSelected: Boolean) {
        val decimalFormat = DecimalFormat("#,###")
        binding.loanAmountValue.setText(priceOfProperty.toString())
        // Set the loan amount in euro or dollar depending on the user choice
        if (isEuroOrDollarSelected) {
            binding.loanAmountTextView.setText(R.string.loan_amount_in_euro) // Loan amount: €
        } else {
            binding.loanAmountTextView.setText(R.string.loan_amount_in_dollar) // Loan amount: $
        }
        // Calculate the monthly payment when the user click on the calculate button and show the monthly payment in euro or dollar depending on the user choice
        binding.calculateButton.setOnClickListener {
            val loanAmountText = binding.loanAmountValue.text.toString()
            val loanAmount = loanAmountText.toIntOrNull()
            val interestRate = binding.interestRateValue.text.toString().toDoubleOrNull()
            val loanDuration = binding.loanDurationValue.text.toString().toIntOrNull()
            val personalContribution = binding.personalContributionValue.text.toString().toIntOrNull()
            showMonthlyPaymentForLoaning(loanAmount, interestRate, loanDuration, personalContribution, decimalFormat, isEuroOrDollarSelected)
        }
    }

    private fun showMonthlyPaymentForLoaning(
        loanAmount: Int?,
        interestRate: Double?,
        loanDuration: Int?,
        personalContribution: Int?,
        decimalFormat: DecimalFormat,
        isEuroOrDollarSelected: Boolean
    ) {
        // Check if the loan amount, the interest rate, the loan duration and the personal contribution are not null and calculate the monthly payment and show it in euro or dollar depending on the user choice
        if (loanAmount != null && interestRate != null && loanDuration != null && personalContribution != null) {
            val monthlyPayment = calculateMonthlyPayment(
                loanAmount,
                interestRate,
                loanDuration,
                personalContribution
            )
            // Format the monthly payment to have a comma every three digits
            val formattedMonthlyPayment = decimalFormat.format(monthlyPayment)
            if (isEuroOrDollarSelected) {
                binding.monthlyPaymentTextView.text =
                    "Monthly payment: $formattedMonthlyPayment€" // Loan amount: €
            } else {
                binding.monthlyPaymentTextView.text =
                    "Monthly payment: $$formattedMonthlyPayment" // Loan amount: $
            }
            binding.monthlyPaymentTextView.setTextColor(context.getColor(R.color.red))
        } else {
            // Show a toast to the user to tell him to enter valid values
            Toast.makeText(context, "Make sure all values entered are valid!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun calculateMonthlyPayment(
        loanAmount: Int,
        interestRate: Double,
        loanDuration: Int,
        personalContribution: Int
    ): Double {
        // Loan amount minus personal contribution
        val loanAmount = loanAmount - personalContribution
        // Monthly interest rate is the annual interest rate divided by twelve (number of months in a year) and then divided again by 100 to get the percentage value
        val monthlyInterestRate = interestRate / 100 / 12
        // Number of payments is the loan duration in years multiplied by twelve (number of months in a year)
        val numberOfPayments = loanDuration * 12
        // Pow means power for mathematical use example of, so 2.pow(3) = 2^3 = 8 same as 2 * 2 * 2 = 8
        // Denominator equal to (1 + monthlyInterestRate)^numberOfPayments - 1 to get the result of the fraction in number of payments
        val denominator = (1 + monthlyInterestRate).pow(numberOfPayments.toDouble()) - 1
        // Return the monthly payment amount using the formula from https://www.thebalance.com/loan-payment-calculations-315564
        return loanAmount * monthlyInterestRate * (1 + monthlyInterestRate).pow(numberOfPayments.toDouble()) / denominator
    }

}