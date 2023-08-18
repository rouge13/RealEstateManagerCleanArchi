package com.openclassrooms.realestatemanager.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.openclassrooms.realestatemanager.data.model.LocationDetails

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class LocationLiveData(private var context: Context) : LiveData<LocationDetails>() {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    companion object {
        // Location update interval in milliseconds (1 minute) - 1000 milliseconds = 1 second - 60000 milliseconds = 1 minute
        private const val LOCATION_UPDATE_INTERVAL : Long = 60000 // 60 seconds
        // Location request configuration
        val locationRequest : LocationRequest = LocationRequest.create().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = LOCATION_UPDATE_INTERVAL / 4
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    // Get the last known location on active of the device and set the value of the LiveData to it
    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location.also {
                setLocationData(it)
            }
        }
        // Start location updates
        startLocationUpdates()
    }

    // Start location updates based on the locationRequest configuration, the locationCallback and the main looper
    @SuppressLint("MissingPermission")
    internal fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult ?: return
            for (location in locationResult.locations){
                setLocationData(location)
            }
        }
    }

    // Set the value of the LiveData to the location
    private fun setLocationData(location: Location?) {
        location?.let { location ->
            value = LocationDetails(location.longitude, location.latitude)
        }
    }

    // Remove location updates when the LiveData is inactive by removing the locationCallback
    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}