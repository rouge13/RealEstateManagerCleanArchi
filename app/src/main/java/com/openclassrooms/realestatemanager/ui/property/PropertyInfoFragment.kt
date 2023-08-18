package com.openclassrooms.realestatemanager.ui.property

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.databinding.FragmentInfoPropertyBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.alertDialog.LoanSimulatorAlertDialog
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedUtilsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyInfoFragment : Fragment() {
    private lateinit var binding: FragmentInfoPropertyBinding
    private val sharedPropertyViewModel: SharedPropertyViewModel by activityViewModels {
        ViewModelFactory(
            requireActivity().application as MainApplication
        )
    }
    private val sharedNavigationViewModel: SharedNavigationViewModel by activityViewModels {
        ViewModelFactory(
            requireActivity().application as MainApplication
        )
    }
    private val sharedUtilsViewModel: SharedUtilsViewModel by activityViewModels {
        ViewModelFactory(
            requireActivity().application as MainApplication
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment with the ViewBinding and return the binding's root view
        binding = FragmentInfoPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Call the displayPropertyDetails method to display the property details in the fragment and display the property photos in the ViewPager2 in the xml layout
        super.onViewCreated(view, savedInstanceState)
        initSharedNavigationViewModelSearchAction()
        displayPropertyDetails(this, view)
    }

    private fun initSharedNavigationViewModelSearchAction() {
        sharedNavigationViewModel.searchClicked.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                // Navigate to search fragment when user click on search button in the toolbar
                val action = PropertyInfoFragmentDirections.actionInfoPropertyFragmentToSearchFragment()
                findNavController().navigate(action)
                sharedNavigationViewModel.doneNavigatingToSearch()
            }
        }
    }

    private fun displayPropertyDetails(fragment: Fragment, view: View) {
            sharedPropertyViewModel.getSelectedProperty.observe(viewLifecycleOwner) { propertyWithDetails ->
                // Show the property details in the fragment and display the property photos in the ViewPager2 in the xml layout and show if sold or not
                propertyWithDetails?.let {
                    binding.fragmentInfoProperty.visibility = View.VISIBLE
                    initIsSoldOrNot(propertyWithDetails)
                    val photoViewPager = binding.photoPropertyViewPager
                    val photoList = propertyWithDetails.photos
                    // Check if photoList is null or empty and if it is the case, display a default photo with a description
                    if (photoList.isNullOrEmpty()) {
                        val defaultPhoto = PhotoEntity(id = -1, photoURI = null, description = getString(R.string.no_photo_description))
                        val adapter = PropertyInfoAdapter(fragment, listOf(defaultPhoto), propertyWithDetails.property?.isSold ?: false)
                        photoViewPager.adapter = adapter
                    } else {
                        val adapter = PropertyInfoAdapter(fragment, photoList, propertyWithDetails.property?.isSold ?: false)
                        photoViewPager.adapter = adapter
                    }
                    lifecycleScope.launch {
                        initStaticMapImageView(propertyWithDetails, view)
                    }
                    initAllTextView(propertyWithDetails)
                    initAllButtons(photoViewPager, propertyWithDetails)
                }
            }
    }

    private suspend fun initStaticMapImageView(propertyWithDetails: PropertyWithDetails, view: View) {
        val staticMapImageView: ImageView = view.findViewById(R.id.static_map_image_view)
        // Check if has location already set else get location from address
        val staticMapUrl: String =
            if ((propertyWithDetails.address?.latitude != null) && (propertyWithDetails.address.longitude != null)) {
                val location = LatLng(propertyWithDetails.address.latitude!!, propertyWithDetails.address.longitude!!)
                val marker = "&markers=color:red%7Alabel:S%7C" + location.latitude + "," + location.longitude
                "https://maps.googleapis.com/maps/api/staticmap?center=" + location.latitude + "," + location.longitude + "&zoom=15&size=400x400&markers=$marker&key=" + getString(R.string.google_map_key)
            } else {
                val addressString = (propertyWithDetails.address?.streetNumber + " " + propertyWithDetails.address?.streetName + " " + propertyWithDetails.address?.city + " " + propertyWithDetails.address?.zipCode + " " + propertyWithDetails.address?.country)
                // Check if address return a location else send a toast to user to tell him that address is not valid
                if (Geocoder(view.context).getFromLocationName(addressString, 1)?.isEmpty() == true) {
                    Toast.makeText(view.context, getString(R.string.address_not_valid), Toast.LENGTH_SHORT).show()
                    ""
                } else {
                    val address = Geocoder(view.context).getFromLocationName(addressString, 1)
                    val location = address?.get(0)?.latitude?.let { it1 -> address[0]?.longitude?.let { it2 -> LatLng(it1, it2) } }
                    propertyWithDetails.address?.let {
                        sharedPropertyViewModel.updateAddressWithLocation(it, latitude = location?.latitude, longitude = location?.longitude)
                    }
                    val marker = "&markers=color:red%7Alabel:S%7C" + location?.latitude + "," + location?.longitude
                    "https://maps.googleapis.com/maps/api/staticmap?center=" + location?.latitude + "," + location?.longitude + "&zoom=15&size=400x400&markers=$marker&key=" + getString(
                        R.string.google_map_key
                    )
                }
            }
        // Check if staticMapUrl is empty or not and if it is the case, display a toast to user to tell him that address is not valid else display the static map in the ImageView
        checkForShowingStaticMap(staticMapUrl, staticMapImageView, view)
    }

    private fun checkForShowingStaticMap(staticMapUrl: String, staticMapImageView: ImageView, view: View) {
        if (staticMapUrl == "") {
            // If staticMapUrl is empty, display a toast to user to tell him that address is not valid and must be modified and hide the static map ImageView to avoid displaying a previous static map
            staticMapImageView.visibility = View.GONE
            Toast.makeText(view.context, getString(R.string.address_not_valid), Toast.LENGTH_SHORT).show()
        } else {
            // If staticMapUrl is not empty, display the static map in the ImageView and show the ImageView
            staticMapImageView.visibility = View.VISIBLE
            Glide.with(view)
                .load(staticMapUrl)
                .centerCrop()
                .into(staticMapImageView)
        }
    }

    private fun initAllTextView(propertyWithDetails: PropertyWithDetails) {
        Log.d("PropertyInfoFragment", "initAllTextView: $propertyWithDetails")
        // Display all the property details in the TextView in the fragment layout and if there is no description, display a default description in the TextView
        binding.squareFeetValue.text = propertyWithDetails.property?.squareFeet.toString()
        binding.roomsValue.text = propertyWithDetails.property?.roomsCount.toString()
        binding.bedroomsValue.text = propertyWithDetails.property?.bedroomsCount.toString()
        binding.bathroomsValue.text = propertyWithDetails.property?.bathroomsCount.toString()
        val description = propertyWithDetails.property?.description
        if (description.isNullOrEmpty()) {
            binding.description.text = getString(R.string.no_description)
        } else {
            binding.description.text = description
        }
        // Create a list of string with all the proximities of the property with the switch elements
        initTheProximitiesForThisProperty(propertyWithDetails)
        initAddressDetails(propertyWithDetails)
    }

    private fun initTheProximitiesForThisProperty(propertyWithDetails: PropertyWithDetails) {
        // Create a list of string with all the proximities of the property with the switch elements and display it in the TextView
        val proximities = mutableListOf<String>()
        propertyWithDetails.property?.schoolProximity?.let { if (it) { proximities.add(getString(R.string.school_proximity)) } }
        propertyWithDetails.property?.parkProximity?.let { if (it) { proximities.add(getString(R.string.park_proximity)) } }
        propertyWithDetails.property?.shoppingProximity?.let { if (it) { proximities.add(getString(R.string.shopping_proximity)) } }
        propertyWithDetails.property?.restaurantProximity?.let { if (it) { proximities.add(getString(R.string.restaurant_proximity)) } }
        propertyWithDetails.property?.publicTransportProximity?.let {
            if (it) {
                proximities.add(getString(R.string.public_transport_proximity))
            }
        }
        // Check if the list is empty or not and if it is the case, display a default text in the TextView else display the list of proximities in the TextView
        if (proximities.isNotEmpty()) {
            val formattedProximitiesString = proximities.joinToString(", ")
            val lastIndex = proximities.size - 1
            val dotFormattedProximitiesString = if (proximities.size > 1) {
                formattedProximitiesString.substring(0, formattedProximitiesString.lastIndexOf(", ")) + ", and " + proximities[lastIndex] + "." } else { "$formattedProximitiesString." }
            binding.propertyProximityText.text = dotFormattedProximitiesString
        } else {
            binding.propertyProximityText.text = getString(R.string.no_proximities)
        }
    }

    private fun initAddressDetails(propertyWithDetails: PropertyWithDetails) {
        // Display all the address details in the TextView in the fragment layout
        Log.d("PropertyInfoFragment", "initAddressDetails: $propertyWithDetails")
        binding.locationLine1.text = (buildString {
            append(propertyWithDetails.address?.streetNumber)
            append(" ")
            append(propertyWithDetails.address?.streetName)
        })
        if (propertyWithDetails.address?.apartmentDetails != "") {
            binding.locationLine2.text =
                (buildString { append(propertyWithDetails.address?.apartmentDetails) })
        }
        binding.locationLine3.text = (buildString { append(propertyWithDetails.address?.city) })
        binding.locationLine4.text = (buildString { append(propertyWithDetails.address?.zipCode) })
        binding.locationLine5.text = (buildString { append(propertyWithDetails.address?.country) })
    }

    @SuppressLint("SetTextI18n")
    private fun initIsSoldOrNot(propertyWithDetails: PropertyWithDetails) {
        Log.d("PropertyInfoFragment", "initIsSoldOrNot: $propertyWithDetails")
        sharedUtilsViewModel.getDateFormatSelected.observe(viewLifecycleOwner) { dateFormat ->
            dateFormat?.let { format ->
                updateDate(propertyWithDetails, format)
            }
        }
    }

    private fun updateDate(propertyWithDetails: PropertyWithDetails, dateFormat: SimpleDateFormat) {
        // Display the date of the sale or the date of the start of the sale in the TextView in the fragment layout and change the color of the text depending on if the property is sold or not
        if (propertyWithDetails.property?.isSold == true) {
            binding.date.setTextColor(resources.getColor(R.color.red))
            val dateSold = propertyWithDetails.property.dateSold?.let { Date(it) }
            binding.date.text = "Sold on : ${dateSold?.let { dateFormat.format(it) }}"
        } else {
            binding.date.setTextColor(resources.getColor(R.color.green))
            val dateStartSelling = propertyWithDetails.property?.dateStartSelling?.let { Date(it) }
            binding.date.text = "Sale date: ${dateStartSelling?.let { dateFormat.format(it) }}"
        }
    }

    private fun initAllButtons(
        photoViewPager: ViewPager2,
        propertyWithDetails: PropertyWithDetails
    ) {
        // Init all the buttons in the fragment layout and set the onClickListeners
        Log.d("PropertyInfoFragment", "initAllButtons: $photoViewPager")
        binding.imageMoveAfterButton.setOnClickListener {
            photoViewPager.currentItem = photoViewPager.currentItem + 1
        }
        binding.imageMoveBeforeButton.setOnClickListener {
            photoViewPager.currentItem = photoViewPager.currentItem - 1
        }
        binding.backwardProperty.setOnClickListener {
            findNavController().popBackStack()
        }
        // Set the onClickListener on the button to display the loan simulator dialog fragment and pass the price of the property to the dialog fragment based on the currency selected by the agent
        sharedUtilsViewModel.getMoneyRateSelected.observe(viewLifecycleOwner) { isEuroSelected ->
            binding.loanSimulatorButton.setOnClickListener {
                val loanSimulatorAlertDialog = LoanSimulatorAlertDialog(requireContext())
                // Check if the price is in euro or dollar and then return the correct price
                if (isEuroSelected) {
                    val convertedPrice = propertyWithDetails.property?.price?.let { it1 ->
                        sharedPropertyViewModel.convertPropertyPrice(it1, isEuroSelected)
                    }
                    if (convertedPrice != null) {
                        loanSimulatorAlertDialog.showLoanSimulator(convertedPrice, isEuroSelected)
                    }
                } else {
                    propertyWithDetails.property?.price?.let { it1 ->
                        loanSimulatorAlertDialog.showLoanSimulator(it1, isEuroSelected)
                    }
                }
            }
        }
    }

    // Check if the device is a tablet or not and if it is the case, hide the backward button in the fragment layout else display it
    override fun onResume() {
        super.onResume()
        if (isDualPanel()) {
            binding.backwardProperty.visibility = View.GONE
        } else {
            binding.backwardProperty.visibility = View.VISIBLE
        }
    }

    // Check if the device is a tablet or not
    private fun isDualPanel(): Boolean {
        return activity?.resources?.getBoolean(R.bool.isTwoPanel) == true
    }
}

