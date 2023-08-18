package com.openclassrooms.realestatemanager.data.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedUtilsViewModel
import com.openclassrooms.realestatemanager.ui.viewmodel.InitializationViewModel

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class ViewModelFactory(private val mainApplication: MainApplication) : ViewModelProvider.Factory {
    // Create ViewModel with repository as parameter if needed that comes from MainApplication class (dependency injection)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedAgentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedAgentViewModel(mainApplication.agentRepository!!, mainApplication) as T
        } else if (modelClass.isAssignableFrom(SharedPropertyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedPropertyViewModel(
                mainApplication.propertyRepository!!,
                mainApplication.addressRepository!!,
                mainApplication.photoRepository!!
            ) as T
        } else if (modelClass.isAssignableFrom(SharedUtilsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedUtilsViewModel() as T
        } else if (modelClass.isAssignableFrom(SharedNavigationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedNavigationViewModel() as T
        } else if (modelClass.isAssignableFrom(InitializationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InitializationViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
