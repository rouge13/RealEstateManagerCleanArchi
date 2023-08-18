package com.openclassrooms.realestatemanager.ui.map

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.LocationDetails
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.property.PropertyInfoFragment
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MapFragment : Fragment() {
    private val agentViewModel: SharedAgentViewModel by activityViewModels { ViewModelFactory(requireActivity().application as MainApplication) }
    private val propertyViewModel: SharedPropertyViewModel by activityViewModels { ViewModelFactory(requireActivity().application as MainApplication) }
    private val sharedNavigationViewModel: SharedNavigationViewModel by activityViewModels { ViewModelFactory(requireActivity().application as MainApplication) }
    private lateinit var fragmentMapBinding: FragmentMapBinding
    private lateinit var googleMap: GoogleMap
    private var agentMarker: Marker? = null
    private val propertyMarkers = mutableMapOf<Marker, PropertyWithDetails>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment with view binding and return the view binding root view
        fragmentMapBinding = FragmentMapBinding.inflate(inflater, container, false)
        return fragmentMapBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get the map fragment and get the map asynchronously to be able to use it in the fragment lifecycle and check if the map is ready to use before using it with one or dual panel
        val mapFragment = if(!isDualPanel()) {childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?} else {childFragmentManager.findFragmentById(R.id.map_600sp) as SupportMapFragment?}
        mapFragment?.getMapAsync { map ->
            googleMap = map
            viewLifecycleOwner.lifecycleScope.launch {
                // Get the location of the agent and update the map with it
                propertyViewModel.getPropertiesWithDetails.collect {
                    setMarkers(it, view)
                }
            }
            agentViewModel.getLocationLiveData().observe(viewLifecycleOwner) {
                if (it != null) {
                    updateMapWithAgentLocation(it)
                }
            }
        }
        initSharedNavigationViewModelSearchAction()
    }

    private fun initSharedNavigationViewModelSearchAction() {
        sharedNavigationViewModel.searchClicked.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                if (!activity?.resources?.getBoolean(R.bool.isTwoPanel)!!) {
                    val action = MapFragmentDirections.actionMapFragmentToSearchFragment()
                    findNavController().navigate(action)
                } else {
                    findNavController().popBackStack()
                }
                sharedNavigationViewModel.doneNavigatingToSearch()
            }
        }
    }

    private fun setMarkers(propertiesWithDetails: List<PropertyWithDetails>, view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            // Get the location of the properties and update the map with it if the location is not null, if it is null, get the location with the address and update the map with it
            val propertyWithCoordinates = propertiesWithDetails.map { propertyWithDetails ->
                if (propertyWithDetails.address?.latitude == null || propertyWithDetails.address.longitude == null) {
                    // Get the location with the address and update the map with it
                    val addressString = (propertyWithDetails.address?.streetNumber + " " + propertyWithDetails.address?.streetName + " " + propertyWithDetails.address?.city + " " + propertyWithDetails.address?.zipCode + " " + propertyWithDetails.address?.country)
                    // Dispatchers.IO because it is a blocking call
                    val location = withContext(Dispatchers.IO) { geocodeAddress(addressString) }
                    propertyWithDetails to location
                } else {
                    // Update the map with the location of the property if it is not null
                    val location = LatLng(propertyWithDetails.address.latitude!!, propertyWithDetails.address.longitude!!)
                    propertyWithDetails to location
                }
            }

            // Clear existing markers
            clearMarkers()
            // Add all the marker to the map with the location of the properties and the location of the agent based of the properties and the agent location
            propertyWithCoordinates.forEach { (propertyWithDetails, location) ->
                initMarker(location, propertyWithDetails)
            }
        }
        // Adding setOnMarkerClickListener after the markers are set to avoid the click listener to be called before the markers are set and the propertyMarkers map is empty
        googleMap.setOnMarkerClickListener { marker ->
            propertyMarkers[marker]?.let { propertyWithDetails ->
                // Set the selected property to the propertyViewModel to be able to use it in the PropertyInfoFragment
                propertyViewModel.setSelectProperty(propertyWithDetails)
                if (!activity?.resources?.getBoolean(R.bool.isTwoPanel)!!){
                    findNavController().navigate(R.id.infoPropertyFragment)
                } else {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.info_fragment_container, PropertyInfoFragment())
                        .commit()
                }
                true
            } ?: false
        }
    }

    // Init Marker
    private fun initMarker(location: LatLng?, propertyWithDetails: PropertyWithDetails) {
        val marker = location?.let {
            // Add the marker to the map with the location of the property
            MarkerOptions().position(it)
                .title(propertyWithDetails.property?.typeOfHouse + "" + propertyWithDetails.property?.id.toString())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        }?.let {
            googleMap.addMarker(it)
        }
        // Add the marker to the map with the location of the property
        marker?.let { propertyMarkers[it] = propertyWithDetails }
    }

    private fun updateMapWithAgentLocation(location: LocationDetails?) {
        location?.let {
            // Update the map with the location of the agent
            val newLocation = LatLng(it.latitude, it.longitude)
            // Add the marker to the map with the location of the agent if it is null, if it is not null, update the marker with the new location
            if (agentMarker == null) {
                agentMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(newLocation)
                        .title(getString(R.string.agent_location))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )
            } else {
                agentMarker?.position = newLocation
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 10f))
        }
    }

    private suspend fun geocodeAddress(addressString: String): LatLng? {
        // Dispatchers.IO because it is a blocking call and it is called in a coroutine scope and try catch because it can throw an IOException if the address is not found
        return withContext(Dispatchers.IO) {
            try {
                // Get the location with the address and update the map with it if the property location was null
                val geocoder = Geocoder(requireContext())
                val addresses = geocoder.getFromLocationName(addressString, 1)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val location = addresses[0]
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Log.d("Geocoding", "Address: $addressString, Latitude: $latitude, Longitude: $longitude")
                        return@withContext LatLng(latitude, longitude)
                    }
                }
            } catch (e: IOException) {
                // Handle geocoding error
                e.printStackTrace()
            }
            return@withContext null
        }
    }

    private fun clearMarkers() {
        // Clear existing markers from the map
        for (marker in propertyMarkers.keys) {
            marker.remove()
        }
        propertyMarkers.clear()
    }

    override fun onResume() {
        // Update the map with the location of the agent when the fragment is resumed
        super.onResume()
        if (isDualPanel()) {
            childFragmentManager.beginTransaction()
                .replace(R.id.info_fragment_container, PropertyInfoFragment())
                .commit()
        }
    }

    // Check if the device is a tablet or a phone
    private fun isDualPanel(): Boolean {
        return resources.getBoolean(R.bool.isTwoPanel)
    }
}
