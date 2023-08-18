package com.openclassrooms.realestatemanager.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.data.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.model.SearchCriteria
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyRepository(private val propertyDao: PropertyDao) {

    // Get all the properties from the database
    val getAllProperties: Flow<List<PropertyEntity>> = propertyDao.getAllProperties()

    // get property by id
    fun getPropertyById(id: Long): PropertyEntity {
        return propertyDao.getPropertyById(id)
    }

    // LiveData for newly inserted property
    private val _insertedProperty = MutableLiveData<PropertyEntity>()
    val insertedProperty: LiveData<PropertyEntity> get() = _insertedProperty

    // Insert a new property using the DAO with suspend function
    suspend fun insert(property: PropertyEntity): Long? {
        property.isSold = false
        val id = propertyDao.insert(property)
        _insertedProperty.value = property // Set the value of insertedProperty LiveData
        return if (id != -1L) id else null
    }

    // Set and return all Filtered properties
    fun getFilteredProperties(searchCriteria: SearchCriteria): Flow<List<PropertyEntity>> {
        return propertyDao.getAllFilteredProperties(
            typesOfHouses = searchCriteria.selectedTypeOfHouseForQuery,
            agentsId = searchCriteria.selectedAgentsIdsForQuery,
            city = searchCriteria.selectedCitiesForQuery,
            boroughs = searchCriteria.selectedBoroughsForQuery,
            minPrice = searchCriteria.selectedMinPriceForQuery,
            maxPrice = searchCriteria.selectedMaxPriceForQuery,
            minSquareFeet = searchCriteria.selectedMinSquareFeetForQuery,
            maxSquareFeet = searchCriteria.selectedMaxSquareFeetForQuery,
            minCountRooms = searchCriteria.selectedMinCountRoomsForQuery,
            maxCountRooms = searchCriteria.selectedMaxCountRoomsForQuery,
            minCountBedrooms = searchCriteria.selectedMinCountBedroomsForQuery,
            maxCountBedrooms = searchCriteria.selectedMaxCountBedroomsForQuery,
            minCountBathrooms = searchCriteria.selectedMinCountBathroomsForQuery,
            maxCountBathrooms = searchCriteria.selectedMaxCountBathroomsForQuery,
            minCountPhotos = searchCriteria.selectedMinCountPhotosForQuery,
            maxCountPhotos = searchCriteria.selectedMaxCountPhotosForQuery,
            startDate = searchCriteria.selectedStartDateForQuery,
            endDate = searchCriteria.selectedEndDateForQuery,
            isSold = searchCriteria.selectedIsSoldForQuery,
            schoolProximity = searchCriteria.selectedSchoolProximityQuery,
            shopProximity = searchCriteria.selectedShopProximityQuery,
            parkProximity = searchCriteria.selectedParkProximityQuery,
            restaurantProximity = searchCriteria.selectedRestaurantProximityQuery,
            publicTransportProximity = searchCriteria.selectedPublicTransportProximityQuery,
            typesOfHousesCount = searchCriteria.selectedTypeOfHouseForQuery.size,
            agentsIdCount = searchCriteria.selectedAgentsIdsForQuery.size,
            cityCount = searchCriteria.selectedCitiesForQuery.size,
            boroughsCount = searchCriteria.selectedBoroughsForQuery.size
        )
    }
    // Update the property using the DAO with suspend function
    suspend fun update(property: PropertyEntity) {
        property.id?.let {
            property.agentId?.let { it1 ->
                propertyDao.updateProperty(
                    it,
                    property.price,
                    property.squareFeet,
                    property.roomsCount,
                    property.bedroomsCount,
                    property.bathroomsCount,
                    property.description,
                    property.typeOfHouse,
                    property.isSold,
                    property.dateStartSelling,
                    property.dateSold,
                    it1,
                    property.primaryPhoto,
                    property.schoolProximity,
                    property.shoppingProximity,
                    property.parkProximity,
                    property.restaurantProximity,
                    property.publicTransportProximity,
                )
            }
        }
    }

    // Update the primary photo of the property
    suspend fun updatePrimaryPhoto(propertyId: Int?, photoUri: String) {
        propertyDao.updatePrimaryPhoto(propertyId, photoUri)
    }
}
