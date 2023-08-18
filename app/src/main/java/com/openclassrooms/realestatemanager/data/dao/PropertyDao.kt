package com.openclassrooms.realestatemanager.data.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Dao
interface PropertyDao {
    // Get all properties
    @Query("SELECT * FROM property ORDER BY id ASC")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    // Get all properties for content provider
    @Query("SELECT * FROM property ORDER BY id ASC")
    fun getAllPropertiesForContentProvider(): Cursor

    // Get property by id
    @Query("SELECT * FROM property WHERE id = :propertyId")
    fun getPropertyById(propertyId: Long): PropertyEntity

    // Get property by id and return a cursor for content provider
    @Query("SELECT * FROM property WHERE id = :propertyId")
    fun getPropertyByIdCursorReturned(propertyId: Long): Cursor

    // Delete property for content provider
    @Query("DELETE FROM property WHERE id = :propertyId")
    fun deletePropertyForContentProvider(propertyId: Long): Int

    // Update property and return number of rows affected
    @Update
    fun updatePropertyForContentProvider(property: PropertyEntity): Int

    @Insert
    fun insertForContentProvider(property: PropertyEntity): Long

    // Insert property and return id of inserted property and must convert later the Long type into Int
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(property: PropertyEntity): Long

    // Get all properties filtered with search criteria
    @Query(
        """
    SELECT property.* FROM property
    INNER JOIN address ON property.id = address.propertyId
    INNER JOIN agent ON property.agentId = agent.id
    WHERE (1 = 1)
    AND (
        CASE
            WHEN (:typesOfHousesCount > 0) THEN typeOfHouse IN (:typesOfHouses)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:agentsIdCount > 0) THEN agentId IN (:agentsId)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:cityCount > 0) THEN city IN (:city)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:boroughsCount > 0) THEN boroughs IN (:boroughs)
            ELSE 1
        END
    )
    AND (:minPrice IS NULL OR price >= :minPrice)
    AND (:maxPrice IS NULL OR price <= :maxPrice)
    AND (:minSquareFeet IS NULL OR squareFeet >= :minSquareFeet)
    AND (:maxSquareFeet IS NULL OR squareFeet <= :maxSquareFeet)
    AND (:minCountRooms IS NULL OR roomsCount >= :minCountRooms)
    AND (:maxCountRooms IS NULL OR roomsCount <= :maxCountRooms)
    AND (:minCountBedrooms IS NULL OR bedroomsCount >= :minCountBedrooms)
    AND (:maxCountBedrooms IS NULL OR bedroomsCount <= :maxCountBedrooms)
    AND (:minCountBathrooms IS NULL OR bathroomsCount >= :minCountBathrooms)
    AND (:maxCountBathrooms IS NULL OR bathroomsCount <= :maxCountBathrooms)
    AND (
        CASE
            WHEN (:isSold IS NOT NULL AND :isSold) THEN 
                ((:startDate IS NULL OR dateSold >= :startDate) AND (:endDate IS NULL OR dateSold <= :endDate))
            ELSE 
                ((:startDate IS NULL OR dateStartSelling >= :startDate) AND (:endDate IS NULL OR dateStartSelling <= :endDate))
        END
    )
    AND (
        CASE
            WHEN :isSold IS NULL THEN 1
            ELSE isSold = :isSold
        END
    )
    AND (
        CASE
            WHEN (:schoolProximity IS NOT NULL) THEN (schoolProximity = :schoolProximity)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:shopProximity IS NOT NULL) THEN (shoppingProximity = :shopProximity)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:parkProximity IS NOT NULL) THEN (parkProximity = :parkProximity)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:restaurantProximity IS NOT NULL) THEN (restaurantProximity = :restaurantProximity)
            ELSE 1
        END
    )
    AND (
        CASE
            WHEN (:publicTransportProximity IS NOT NULL) THEN (publicTransportProximity = :publicTransportProximity)
            ELSE 1
        END
    )
    AND (
        (:minCountPhotos IS NULL AND :maxCountPhotos IS NULL) -- When both min and max are null, include all properties
        OR (
            property.id IN (
            SELECT propertyId FROM photo
            GROUP BY propertyId
            HAVING (:minCountPhotos IS NULL OR COUNT(id) >= :minCountPhotos)
            AND (:maxCountPhotos IS NULL OR COUNT(id) <= :maxCountPhotos)
            )
        )
    )
    AND (:isSold IS NULL OR isSold = :isSold)
    ORDER BY property.id ASC
    """
    )
    fun getAllFilteredProperties(
        typesOfHouses: List<String>?,
        agentsId: List<Int>?,
        city: List<String>?,
        boroughs: List<String>?,
        minPrice: Int?,
        maxPrice: Int?,
        minSquareFeet: Int?,
        maxSquareFeet: Int?,
        minCountRooms: Int?,
        maxCountRooms: Int?,
        minCountBedrooms: Int?,
        maxCountBedrooms: Int?,
        minCountBathrooms: Int?,
        maxCountBathrooms: Int?,
        minCountPhotos: Int?,
        maxCountPhotos: Int?,
        startDate: Long?,
        endDate: Long?,
        isSold: Boolean?,
        schoolProximity: Boolean?,
        shopProximity: Boolean?,
        parkProximity: Boolean?,
        restaurantProximity: Boolean?,
        publicTransportProximity: Boolean?,
        typesOfHousesCount: Int?,
        agentsIdCount: Int?,
        cityCount: Int?,
        boroughsCount: Int?
    ): Flow<List<PropertyEntity>>

    // Update property
    @Query(
        """
    UPDATE property
    SET
        price = :price,
        squareFeet = :squareFeet,
        roomsCount = :roomsCount,
        bedroomsCount = :bedroomsCount,
        bathroomsCount = :bathroomsCount,
        description = :description,
        typeOfHouse = :typeOfHouse,
        isSold = :isSold,
        dateStartSelling = :dateStartSelling,
        dateSold = :dateSold,
        agentId = :agentId,
        primaryPhoto = :primaryPhoto,
        schoolProximity = :schoolProximity,
        parkProximity = :parkProximity,
        shoppingProximity = :shoppingProximity,
        restaurantProximity = :restaurantProximity,
        publicTransportProximity = :publicTransportProximity
    WHERE id = :propertyId
    """
    )
    suspend fun updateProperty(    propertyId: Int,
                                   price: Int?,
                                   squareFeet: Int?,
                                   roomsCount: Int?,
                                   bedroomsCount: Int?,
                                   bathroomsCount: Int?,
                                   description: String?,
                                   typeOfHouse: String?,
                                   isSold: Boolean?,
                                   dateStartSelling: Long?,
                                   dateSold: Long?,
                                   agentId: Int,
                                   primaryPhoto: String?,
                                   schoolProximity: Boolean?,
                                   parkProximity: Boolean?,
                                   shoppingProximity: Boolean?,
                                   restaurantProximity: Boolean?,
                                   publicTransportProximity: Boolean?)

    // Update primary photo of the property
    @Query("UPDATE property SET primaryPhoto = :primaryPhotoURI WHERE id = :propertyId")
    suspend fun updatePrimaryPhoto(propertyId: Int?, primaryPhotoURI: String)
}