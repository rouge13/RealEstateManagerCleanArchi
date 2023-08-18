package com.openclassrooms.realestatemanager.ui.propertyList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.databinding.FragmentPropertyListBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.property.PropertyInfoFragment
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedUtilsViewModel
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyListFragment : Fragment() {
    private lateinit var binding: FragmentPropertyListBinding
    private lateinit var adapter: PropertyListAdapter
    private val propertyListViewModel: SharedPropertyViewModel by activityViewModels { ViewModelFactory(requireActivity().application as MainApplication) }
    private val sharedNavigationViewModel: SharedNavigationViewModel by activityViewModels { ViewModelFactory(requireActivity().application as MainApplication) }
    private val sharedUtilsViewModel: SharedUtilsViewModel by activityViewModels { ViewModelFactory(requireActivity().application as MainApplication) }
    private val sharedPropertyViewModel: SharedPropertyViewModel by activityViewModels { ViewModelFactory(requireActivity().application as MainApplication) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPropertyListBinding.inflate(inflater, container, false)
        // Configure the adapter for the recyclerView with the DiffUtil callback and the onPropertyClick lambda function to navigate to the PropertyInfoFragment when a property is clicked
        adapter = PropertyListAdapter(viewLifecycleOwner,sharedPropertyViewModel, sharedUtilsViewModel ,object : DiffUtil.ItemCallback<PropertyWithDetails>() {
            // Compare the id of the properties to know if they are the same or not (to know if the property has been updated or not) and compare the id of the properties
            override fun areItemsTheSame(oldItem: PropertyWithDetails, newItem: PropertyWithDetails): Boolean {
                return oldItem.property?.id == newItem.property?.id
            }
            // Compare the content of the properties to know if they are the same or not (to know if the property has been updated or not) and compare the content of the properties
            override fun areContentsTheSame(oldItem: PropertyWithDetails, newItem: PropertyWithDetails): Boolean {
                return oldItem.property == newItem.property && oldItem.address == newItem.address && oldItem.photos == newItem.photos
            }
        }, onPropertyClick = { propertyWithDetails ->
            // Navigate to the PropertyInfoFragment when a property is clicked and set the property in the ViewModel to be able to display it in the PropertyInfoFragment
            navigateToInfoPropertyFragment(propertyWithDetails)
        })
        // Set the layout manager for the recyclerView
        binding.fragmentPropertyListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Set the adapter for the recyclerView
        binding.fragmentPropertyListRecyclerView.adapter = adapter
        // Configure the recyclerView
        configureRecyclerView()
        initSharedNavigationViewModelSearchAction()
        return binding.root
    }
    private fun initSharedNavigationViewModelSearchAction() {
        sharedNavigationViewModel.searchClicked.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                val action = PropertyListFragmentDirections.actionPropertyListFragmentToSearchFragment()
                findNavController().navigate(action)
                sharedNavigationViewModel.doneNavigatingToSearch()
                // If we are navigating to the search fragment, we should set the SearchCriteria to null so that all properties are fetched by default
                propertyListViewModel.setSearchCriteria(null)
            }
        }
    }
    private fun configureRecyclerView() {
        // Observe the combined data from the ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            propertyListViewModel.getPropertiesWithDetails.collect { propertiesWithDetails ->
                adapter.submitList(propertiesWithDetails)
            }
        }
    }
    private fun navigateToInfoPropertyFragment(propertyWithDetails: PropertyWithDetails) {
        // Navigate to the PropertyInfoFragment when a property is clicked and set the property in the ViewModel to be able to display it in the PropertyInfoFragment
        propertyListViewModel.setSelectProperty(propertyWithDetails)
        if (!isDualPanel()) {
            findNavController().navigate(R.id.infoPropertyFragment)
        } else {
            childFragmentManager.beginTransaction()
                .replace(R.id.info_fragment_container, PropertyInfoFragment())
                .commit()
        }
    }

    // When the fragment is resumed, if we are in dual panel mode, we should display the PropertyInfoFragment
    override fun onResume() {
        super.onResume()
        if (isDualPanel()) {
            childFragmentManager.beginTransaction()
                .replace(R.id.info_fragment_container, PropertyInfoFragment())
                .commit()
        }
    }

    // Check if we are in dual panel mode
    private fun isDualPanel(): Boolean {
        return resources.getBoolean(R.bool.isTwoPanel)
    }
}