package com.openclassrooms.realestatemanager.data.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Dao
interface PhotoDao {
    // Get all photos
    @Query("SELECT * FROM photo ORDER BY id ASC")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    // Get all photos related to a property
    @Query("SELECT * FROM photo WHERE propertyId IS NULL OR propertyId = :propertyId")
    suspend fun getAllPhotosRelatedToASpecificProperty(propertyId: Int? = null): List<PhotoEntity>

    // Insert photo for content provider
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPhotoForContentProvider(photo: PhotoEntity): Long

    // Insert photo
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(photo: PhotoEntity): Long

    // Update photo with the propertyId
    @Query(
        """
        UPDATE photo 
        SET propertyId = :propertyId 
        WHERE id = :photoId
        """
    )
    suspend fun updatePhotoWithPropertyId(
        propertyId: Int,
        photoId: Int
    )

    // Delete photo
    @Query("DELETE FROM photo WHERE id = :photoId")
    suspend fun deletePhoto(photoId: Int)

    // Delete photos with propertyId equal to null
    @Query("DELETE FROM photo WHERE propertyId IS NULL")
    suspend fun deletePhotosWithNullPropertyId()

    // Update is Primary photo with the photoId
    @Query(
        """
        UPDATE photo 
        SET isPrimaryPhoto = :isPrimary 
        WHERE id = :photoId
        """
    )
    suspend fun updateIsPrimaryPhoto(
        isPrimary: Boolean,
        photoId: Int
    )


}