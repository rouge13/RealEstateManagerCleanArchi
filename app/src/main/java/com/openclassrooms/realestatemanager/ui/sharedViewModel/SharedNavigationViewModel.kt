package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SharedNavigationViewModel : ViewModel() {
    // Navigation to Search Fragment from any other fragment
    private val _searchClicked = MutableLiveData<Boolean>()
    val searchClicked: LiveData<Boolean> get() = _searchClicked

    fun doneNavigatingToSearch() {
        _searchClicked.value = false
    }

    // Navigation to add or modify fragment from any other fragment false is Add and true is Modify
    private val _addOrModifyClicked = MutableLiveData<Boolean>()
    val getAddOrModifyClicked: LiveData<Boolean> get() = _addOrModifyClicked
    fun setAddOrModifyClicked(isModify: Boolean) {
        _addOrModifyClicked.value = isModify
    }

    // Navigation offline or online with internet for getting location by address
    private val _onlineClicked = MutableLiveData<Boolean>()
    val getOnlineClicked: LiveData<Boolean> get() = _onlineClicked
    fun setOnlineNavigation(isOnline: Boolean) {
        _onlineClicked.value = isOnline
    }
}