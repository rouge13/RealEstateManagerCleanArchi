package com.openclassrooms.realestatemanager.ui.alertDialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.DialogSettingsUtilsBinding
import com.openclassrooms.realestatemanager.ui.MainActivity
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedUtilsViewModel
import com.openclassrooms.realestatemanager.ui.utils.Utils
import java.text.SimpleDateFormat

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SettingsCurrencyDateAlertDialog(
    private val context: Context,
    private val sharedUtilsViewModel: SharedUtilsViewModel
) {

    private var alertDialog: AlertDialog? = null
    private lateinit var binding: DialogSettingsUtilsBinding

    fun showSettings() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.settings)
        val inflater = LayoutInflater.from(context)
        binding = DialogSettingsUtilsBinding.inflate(inflater)
        builder.setView(binding.root)
        // Get the date format selected by the agent in the settings and init the radio button selected with it (us or fr)
        sharedUtilsViewModel.getDateFormatSelected.observe(context as MainActivity) { initDateFormatSelected(it) }
        // Init the radio button selected with the money rate selected by the agent in the settings too
        initMoneyRateSelected()
        // Setup the listener for the button to save the new currency or date format selected by the agent in the settings
        builder.setPositiveButton("Save") { dialog, _ ->
            setConvertMoneyValidate()
            setSimpleDateFormat()
            dialog.dismiss()
        }
        // Cancel the dialog
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        // Create and show the alert dialog to the agent
        alertDialog = builder.create()
        alertDialog?.show()
        // Setup the listeners for the radio buttons
        setupListeners()
    }


    private fun initMoneyRateSelected() {
        // Init the radio button selected with the money rate selected by the agent in the settings
        if (sharedUtilsViewModel.getMoneyRateSelected.value == true) {
            binding.radioButtonDollars.isChecked = false
            binding.radioButtonEuros.isChecked = true
        } else {
            binding.radioButtonEuros.isChecked = false
            binding.radioButtonDollars.isChecked = true
        }
    }

    private fun setSimpleDateFormat() {
        // Set the date format selected by the agent in the settings when the agent click on the button to save the new date format selected
        val dateFormatSelected = if (binding.usDateFormatButton.isChecked) {
            Utils.todayDateUsaFormat
        } else {
            Utils.todayDateFranceFormat
        }
        sharedUtilsViewModel.setDateFormatSelected(dateFormatSelected)
    }

    private fun initDateFormatSelected(dateFormat: SimpleDateFormat?) {
        // Init the radio button selected with the date format selected by the agent in the settings
        val dateFormatSelected = dateFormat?.toPattern()
        if (dateFormatSelected == "yyyy/MM/dd") {
            binding.usDateFormatButton.isChecked = true
        } else {
            binding.euDateFormatButton.isChecked = true
        }
    }

    private fun setupListeners() {
        // Setup the listener for the button to save the new currency selected by the agent in the settings
        setConvertMoney()
        // Update the date format selected by the agent in the settings
        setDateFormat()
    }

    private fun setConvertMoney() {
        // Set the currency selected by the agent in the settings (dollars or euros)
        binding.radioButtonDollars.setOnClickListener {
            binding.radioButtonEuros.isSelected = false
            binding.radioButtonDollars.isSelected = true
        }
        binding.radioButtonEuros.setOnClickListener {
            binding.radioButtonEuros.isSelected = true
            binding.radioButtonDollars.isSelected = false
        }
    }

    private fun setConvertMoneyValidate() {
        // Set the currency selected by the agent in the settings (dollars or euros) when the agent click on the save button
        val isEurosSelected = binding.radioButtonEuros.isChecked
        sharedUtilsViewModel.setActiveSelectionMoneyRate(isEurosSelected)
    }

    private fun setDateFormat() {
        // Set the date format selected by the agent in the settings (us or fr)
        binding.usDateFormatButton.setOnClickListener {
            binding.usDateFormatButton.isSelected = true
            binding.euDateFormatButton.isSelected = false
        }
        binding.euDateFormatButton.setOnClickListener {
            binding.euDateFormatButton.isSelected = true
            binding.usDateFormatButton.isSelected = false
        }
    }
}