package com.openclassrooms.realestatemanager.ui.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.ui.MainApplication
import kotlinx.coroutines.launch

class InitializationViewModel : ViewModel() {
    fun startInitialization(application: MainApplication) {
        viewModelScope.launch {
            application.waitForInitialization()
        }
    }
}