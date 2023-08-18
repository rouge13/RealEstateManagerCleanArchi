package com.openclassrooms.realestatemanager.data.repository

import com.openclassrooms.realestatemanager.data.dao.PhotoDao
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PhotoRepository(private val photoDao: PhotoDao) {

    // Get all the propertiesPhoto from the database
    val getAllPhotos: Flow<List<PhotoEntity>> = photoDao.getAllPhotos()

    // Insert Photo
    suspend fun insert(photo: PhotoEntity): Long? {
           return photoDao.insert(photo)
    }

    // Get all photos related to a property
    suspend fun getAllPhotosRelatedToASpecificProperty(propertyId: Int? = null): List<PhotoEntity>? {
        return photoDao.getAllPhotosRelatedToASpecificProperty(propertyId)
    }

    // Update photo with the propertyId
    suspend fun updatePhotoWithPropertyId(photoId: Int, propertyId: Int) {
        photoDao.updatePhotoWithPropertyId(photoId = photoId, propertyId = propertyId)
    }

    // Update is Primary photo with the photoId
    suspend fun updateIsPrimaryPhoto(isPrimary: Boolean, photoId: Int) {
        photoDao.updateIsPrimaryPhoto(isPrimary = isPrimary, photoId = photoId)
    }

    // Delete photo
    suspend fun deletePhoto(photoId: Int) {
        photoDao.deletePhoto(photoId = photoId)
    }

    // Delete all photos with null propertyId if the property is not created
    suspend fun deletePhotosWithNullPropertyId() {
        photoDao.deletePhotosWithNullPropertyId()
    }

}
