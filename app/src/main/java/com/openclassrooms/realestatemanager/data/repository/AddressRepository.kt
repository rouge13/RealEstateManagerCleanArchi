package com.openclassrooms.realestatemanager.data.repository

import com.openclassrooms.realestatemanager.data.dao.AddressDao
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AddressRepository(private val addressDao: AddressDao) {

    // Get all the propertiesAddress from the database
    val getAllAddresses: Flow<List<AddressEntity>> = addressDao.getAllAddress()

    // Insert Address
    suspend fun insert(address: AddressEntity) {
            addressDao.insert(address)
    }

    // Update Address
    suspend fun updateAddress(address: AddressEntity) {
        address.id?.let {
            addressDao.updateAddress(
                it,
                address.apartmentDetails,
                address.streetNumber,
                address.streetName,
                address.city,
                address.boroughs,
                address.zipCode,
                address.country,
                address.latitude,
                address.longitude
            )
        }
    }
}