package com.openclassrooms.realestatemanager.data.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Dao
interface AddressDao {
    // Get all address
    @Query("SELECT * FROM address ORDER BY id ASC")
    fun getAllAddress(): Flow<List<AddressEntity>>
    // Insert Address
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(address: AddressEntity)

    // Insert address for content provider
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAddressForContentProvider(address: AddressEntity): Long

    // Get address by properyId
    @Query("SELECT * FROM address WHERE propertyId = :propertyId")
    fun getAddressRelatedToASpecificProperty(propertyId: Int): Flow<AddressEntity?>

    // Update Address
    @Query(
        """
    UPDATE address
    SET apartmentDetails = :apartmentDetails,
        streetNumber = :streetNumber,
        streetName = :streetName,
        city = :city,
        boroughs = :boroughs,
        zipCode = :zipCode,
        country = :country,
        latitude = :latitude,
        longitude = :longitude
    WHERE id = :addressId
    """
    )
    suspend fun updateAddress(
        addressId: Int,
        apartmentDetails: String?,
        streetNumber: String?,
        streetName: String?,
        city: String?,
        boroughs: String?,
        zipCode: String?,
        country: String?,
        latitude: Double?,
        longitude: Double?
    )
}