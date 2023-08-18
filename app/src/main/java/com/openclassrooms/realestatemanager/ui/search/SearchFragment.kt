package com.openclassrooms.realestatemanager.ui.search

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.model.SearchCriteria
import com.openclassrooms.realestatemanager.databinding.FragmentSearchPropertyBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedUtilsViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchPropertyBinding
    private val sharedPropertyViewModel: SharedPropertyViewModel by activityViewModels {
        ViewModelFactory(
            requireActivity().application as MainApplication
        )
    }
    private val sharedAgentViewModel: SharedAgentViewModel by activityViewModels {
        ViewModelFactory(
            requireActivity().application as MainApplication
        )
    }
    private val sharedUtilsViewModel: SharedUtilsViewModel by activityViewModels {
        ViewModelFactory(
            requireActivity().application as MainApplication
        )
    }
    private val searchCriteria = SearchCriteria()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAllButtons()
        getAllValuesEdited()
        initTypeOfHouseBoroughsAndCities()
        initAgentsNames()
        initAllSwitches()
    }

    private fun getAllValuesEdited() {
        startPriceValue()
        endPriceValue()
        startSquareFeetValue()
        endSquareFeetValue()
        startRoomsNumberValue()
        endRoomsNumberValue()
        startBedroomsNumberValue()
        endBedroomsNumberValue()
        startBathroomsNumberValue()
        endBathroomsNumberValue()
        startPhotoNumberValue()
        endPhotoNumberValue()
    }

    private fun endPhotoNumberValue() {
        binding.propertyPhotosNumberEndValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyPhotosNumberEndValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMaxCountPhotosForQuery =
                        binding.propertyPhotosNumberEndValue.text.toString().toInt()
                }
            }
    }

    private fun startPhotoNumberValue() {
        binding.propertyPhotosNumberStartValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyPhotosNumberStartValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMinCountPhotosForQuery =
                        binding.propertyPhotosNumberStartValue.text.toString().toInt()
                }
            }
    }

    private fun endBathroomsNumberValue() {
        binding.propertyBathroomsNumberEndValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyBathroomsNumberEndValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMaxCountBathroomsForQuery =
                        binding.propertyBathroomsNumberEndValue.text.toString().toInt()
                }
            }
    }

    private fun startBathroomsNumberValue() {
        binding.propertyBathroomsNumberStartValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyBathroomsNumberStartValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMinCountBathroomsForQuery =
                        binding.propertyBathroomsNumberStartValue.text.toString().toInt()
                }
            }
    }

    private fun endBedroomsNumberValue() {
        binding.propertyBedroomsNumberEndValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyBedroomsNumberEndValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMaxCountBedroomsForQuery =
                        binding.propertyBedroomsNumberEndValue.text.toString().toInt()
                }
            }
    }

    private fun startBedroomsNumberValue() {
        binding.propertyBedroomsNumberStartValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyBedroomsNumberStartValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMinCountBedroomsForQuery =
                        binding.propertyBedroomsNumberStartValue.text.toString().toInt()
                }
            }
    }

    private fun endRoomsNumberValue() {
        binding.propertyRoomsNumberEndValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyRoomsNumberEndValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMaxCountRoomsForQuery =
                        binding.propertyRoomsNumberEndValue.text.toString().toInt()
                }
            }
    }

    private fun startRoomsNumberValue() {
        binding.propertyRoomsNumberStartValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyRoomsNumberStartValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMinCountRoomsForQuery =
                        binding.propertyRoomsNumberStartValue.text.toString().toInt()
                }
            }
    }

    private fun endSquareFeetValue() {
        binding.propertySquareFeetEndValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertySquareFeetEndValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMaxSquareFeetForQuery =
                        binding.propertySquareFeetEndValue.text.toString().toInt()
                }
            }
    }

    private fun startSquareFeetValue() {
        binding.propertySquareFeetStartValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertySquareFeetStartValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMinSquareFeetForQuery =
                        binding.propertySquareFeetStartValue.text.toString().toInt()
                }
            }
    }

    private fun endPriceValue() {
        sharedUtilsViewModel.getMoneyRateSelected.observe(viewLifecycleOwner) { isEuroSelected ->
            val convertToDollar: Boolean = !isEuroSelected
            binding.propertyPriceEndValue.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus && binding.propertyPriceEndValue.text!!.isNotEmpty()) {
                        if (isEuroSelected) {
                            searchCriteria.selectedMaxPriceForQuery =
                                sharedPropertyViewModel.convertPropertyPrice(binding.propertyPriceEndValue.text.toString().toInt(), convertToDollar)
                        } else {
                            searchCriteria.selectedMaxPriceForQuery = binding.propertyPriceEndValue.text.toString().toInt()
                        }
                    }
                }
        }
    }

    private fun startPriceValue() {
        sharedUtilsViewModel.getMoneyRateSelected.observe(viewLifecycleOwner) { isEuroSelected ->
            val convertToDollar: Boolean = !isEuroSelected
            if (isEuroSelected) {
                binding.propertyPriceStartText.setText(R.string.price_start_euro)
            } else {
                binding.propertyPriceStartText.setText(R.string.price_start_dollar)
            }
            binding.propertyPriceStartValue.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus && binding.propertyPriceStartValue.text!!.isNotEmpty()) {
                        if (isEuroSelected) {
                            searchCriteria.selectedMinPriceForQuery =
                                sharedPropertyViewModel.convertPropertyPrice(binding.propertyPriceStartValue.text.toString().toInt(), convertToDollar)
                        } else {
                            searchCriteria.selectedMinPriceForQuery = binding.propertyPriceStartValue.text.toString().toInt()
                        }
                    }
                }
        }
    }

    private fun initAllSwitches() {
        initSchoolProximitySwitch()
        initShopProximitySwitch()
        initParkProximitySwitch()
        initRestaurantProximitySwitch()
        initPublicTransportProximitySwitch()
        initSoldSwitch()
        initForSaleSwitch()
    }

    private fun initForSaleSwitch() {
        binding.switchForSale.setOnCheckedChangeListener { _, _ ->
            searchCriteria.selectedIsSoldForQuery = false
            binding.switchSold.isChecked = false
        }
    }

    private fun initSoldSwitch() {
        binding.switchSold.setOnCheckedChangeListener { _, isChecked ->
            searchCriteria.selectedIsSoldForQuery = isChecked
            binding.switchForSale.isChecked = false
        }
    }

    private fun initPublicTransportProximitySwitch() {
        binding.switchTransport.setOnCheckedChangeListener { _, isChecked ->
            searchCriteria.selectedPublicTransportProximityQuery = isChecked
        }
    }

    private fun initRestaurantProximitySwitch() {
        binding.switchRestaurant.setOnCheckedChangeListener { _, isChecked ->
            searchCriteria.selectedRestaurantProximityQuery = isChecked
        }
    }

    private fun initParkProximitySwitch() {
        binding.switchPark.setOnCheckedChangeListener { _, isChecked ->
            searchCriteria.selectedParkProximityQuery = isChecked
        }
    }

    private fun initShopProximitySwitch() {
        binding.switchShopping.setOnCheckedChangeListener { _, isChecked ->
            searchCriteria.selectedShopProximityQuery = isChecked
        }
    }

    private fun initSchoolProximitySwitch() {
        binding.switchSchool.setOnCheckedChangeListener { _, isChecked ->
            searchCriteria.selectedSchoolProximityQuery = isChecked
        }
    }

    private fun updateSelectedAgentsIds(selectedAgentsNames: List<String>) {
        sharedAgentViewModel.allAgents.observe(viewLifecycleOwner) { agents ->
            val selectedAgentsIds = selectedAgentsNames.mapNotNull { selectedNames ->
                agents.firstOrNull { it.name == selectedNames }?.id
            }
            searchCriteria.selectedAgentsIdsForQuery = selectedAgentsIds
        }
    }

    private fun initAgentsNames() {
        sharedAgentViewModel.allAgents.observe(viewLifecycleOwner) { agents ->
            val agentsNames = agents.map { it.name }.distinct()
            initAgentsNames(agentsNames.toMutableList())
        }
    }

    private fun initAgentsNames(agentsNames: MutableList<String?>) {
        val multiAutoCompleteTextView = binding.propertyAgentSellerMultiAutoComplete
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            agentsNames
        )
        multiAutoCompleteTextView.setAdapter(adapter)
        multiAutoCompleteTextView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        multiAutoCompleteTextView.threshold = 2 // Start suggesting after typing one character
        multiAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            val selectedAgentsNames = multiAutoCompleteTextView.text
                .split(",").map { it.trim() }.filter { it.isNotEmpty() }
            updateSelectedAgentsIds(selectedAgentsNames)
        }
    }

    private fun initTypeOfHouseBoroughsAndCities() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedPropertyViewModel.getPropertiesWithDetails.collect { propertiesWithDetails ->
                val typesOfHouse =
                    propertiesWithDetails.mapNotNull { it.property?.typeOfHouse }.distinct()
                val boroughs = propertiesWithDetails.mapNotNull { it.address?.boroughs }.distinct()
                val cities = propertiesWithDetails.mapNotNull { it.address?.city }.distinct()
                initTypesOfHouse(typesOfHouse)
                initBoroughs(boroughs)
                initCities(cities)
            }
        }

    }

    private fun initCities(cities: List<String>) {
        // Init cities autocomplete text view with the list of cities from the database and update the search criteria when the user selects a city from the list
        val multiAutoCompleteTextView = binding.propertyCityMultiAutoComplete
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)
        multiAutoCompleteTextView.setAdapter(adapter)
        multiAutoCompleteTextView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        multiAutoCompleteTextView.threshold = 2 // Start suggesting after typing one character
        multiAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            val selectedCities = multiAutoCompleteTextView.text
                .split(",").map { it.trim() }.filter { it.isNotEmpty() }
            searchCriteria.selectedCitiesForQuery = selectedCities
        }
    }

    private fun initBoroughs(boroughs: List<String>) {
        // Init boroughs autocomplete text view with the list of boroughs from the database and update the search criteria when the user selects a borough from the list
        val multiAutoCompleteTextView = binding.propertyBoroughsMultiAutoComplete
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            boroughs
        )
        multiAutoCompleteTextView.setAdapter(adapter)
        multiAutoCompleteTextView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        // Start suggesting after typing one character
        multiAutoCompleteTextView.threshold = 2
        multiAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            val selectedBoroughs = multiAutoCompleteTextView.text
                .split(",").map { it.trim() }.filter { it.isNotEmpty() }
            searchCriteria.selectedBoroughsForQuery = selectedBoroughs
        }
    }

    private fun initTypesOfHouse(typesOfHouse: List<String>) {
        // Init types of house autocomplete text view with the list of types of house from the database and update the search criteria when the user selects a type of house from the list
        val multiAutoCompleteTextView = binding.propertyTypeOfHouseMultiAutoComplete
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            typesOfHouse
        )
        multiAutoCompleteTextView.setAdapter(adapter)
        multiAutoCompleteTextView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        // Start suggesting after typing one character
        multiAutoCompleteTextView.threshold = 2
        multiAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            val selectedTypeOfHouse = multiAutoCompleteTextView.text
                .split(",").map { it.trim() }.filter { it.isNotEmpty() }
            searchCriteria.selectedTypeOfHouseForQuery = selectedTypeOfHouse
        }
    }

    private fun initAllButtons() {
        // Init all buttons and set their onClickListener
        binding.cancelButton.setOnClickListener {
            // if the user cancels the search, we reset the search criteria to the previous search criteria (if there is one)
            if (sharedPropertyViewModel.previousSearchCriteria.value != null) {
                sharedPropertyViewModel.setSearchCriteria(sharedPropertyViewModel.previousSearchCriteria.value)
            }
            findNavController().popBackStack()
        }
        binding.searchProperty.setOnClickListener {
            // if the user searches for a property, we save the current search criteria as the previous search criteria
            sharedPropertyViewModel.setSearchCriteria(searchCriteria)
            if (!activity?.resources?.getBoolean(R.bool.isTwoPanel)!!) {
                binding.root.findNavController().navigate(R.id.propertyListFragment)
            } else {
                findNavController().popBackStack()
            }
        }
        binding.removingFilter.setOnClickListener {
            // Reset your search criteria
            searchCriteria.clear()
            // Request all properties from the ViewModel
            sharedPropertyViewModel.setSelectProperty(null)
            sharedPropertyViewModel.setSearchCriteria(null)
            findNavController().popBackStack()
        }
        initStartDate()
        initEndDate()
    }

    private fun initEndDate() {
        binding.btnPropertyDateEnd.setOnClickListener {
            // Get the current date as a Calendar instance
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getTimeZone("UTC")
            // Create a DatePickerDialog with the current date as the default date
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    // When a date is selected, update the EditText with the selected date
                    val selectedMonth = month + 1 // Add 1 to the month value
                    val selectedDate = "$selectedMonth/$dayOfMonth/$year"
                    sharedUtilsViewModel.getDateFormatSelected.observe(viewLifecycleOwner) {
                        binding.propertyDateEndText.text = it.format(selectedDate)
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            // Set the value of the selected date to the search criteria when the user dismisses the dialog (clicks on OK)
            datePickerDialog.setOnDismissListener {
                calendar.apply {
                    set(Calendar.YEAR, datePickerDialog.datePicker.year)
                    set(Calendar.MONTH, datePickerDialog.datePicker.month)
                    set(Calendar.DAY_OF_MONTH, datePickerDialog.datePicker.dayOfMonth)
                }
                searchCriteria.selectedEndDateForQuery = calendar.timeInMillis
            }
            // Show the date picker dialog
            datePickerDialog.show()
        }
    }

    private fun initStartDate() {
        binding.btnPropertyDateStart.setOnClickListener {
            // Get the current date as a Calendar instance
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getTimeZone("UTC")
            // Create a DatePickerDialog with the current date as the default date
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedMonth = month + 1 // Add 1 to the month value
                    val selectedDate = "$selectedMonth/$dayOfMonth/$year"
                    // When a date is selected, update the EditText with the selected date
                    sharedUtilsViewModel.getDateFormatSelected.observe(viewLifecycleOwner) {
                        binding.propertyDateStartText.text = it.format(selectedDate)
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            // Set the value of the selected date to the search criteria when the user dismisses the dialog (clicks on OK)
            datePickerDialog.setOnDismissListener {
                calendar.apply {
                    set(Calendar.YEAR, datePickerDialog.datePicker.year)
                    set(Calendar.MONTH, datePickerDialog.datePicker.month)
                    set(Calendar.DAY_OF_MONTH, datePickerDialog.datePicker.dayOfMonth)
                }
                searchCriteria.selectedStartDateForQuery = calendar.timeInMillis

            }
            // Show the date picker dialog
            datePickerDialog.show()
        }
    }
}