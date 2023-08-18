package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SharedUtilsViewModel : ViewModel() {
    // Set the active selection of the moneyRate in the database
    private val _isInEuro = MutableLiveData<Boolean>()
    fun setActiveSelectionMoneyRate(activeSelection: Boolean) {
        _isInEuro.value = activeSelection
    }
    // Get the money rate selected by the agent
    val getMoneyRateSelected: LiveData<Boolean> get(){
        return _isInEuro
    }

    // Date format selected by the agent in the settings with the getter and the setter
    private val _dateFormatSelected = MutableLiveData<SimpleDateFormat>()
    val getDateFormatSelected: LiveData<SimpleDateFormat> get() = _dateFormatSelected
    fun setDateFormatSelected(dateFormatSelected: SimpleDateFormat) {
        _dateFormatSelected.value = dateFormatSelected
    }
}

