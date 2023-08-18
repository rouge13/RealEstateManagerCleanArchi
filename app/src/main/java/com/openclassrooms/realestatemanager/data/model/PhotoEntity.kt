package com.openclassrooms.realestatemanager.data.model

import android.content.ContentValues
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Parcelize
@Entity(
    tableName = "photo",
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["propertyId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var photoURI: String? = null,
    var description: String? = null,
    var propertyId: Int? = null,
    var isPrimaryPhoto: Boolean = false
) : Parcelable {

    companion object {
        // Getting content values for content provider from property entity
        fun fromContentValues(values: ContentValues): PhotoEntity{
            val photo = PhotoEntity()
            if (values.containsKey("id")) photo.id = values.getAsInteger("id")
            if (values.containsKey("photoURI")) photo.photoURI = values.getAsString("photoURI")
            if (values.containsKey("description")) photo.description = values.getAsString("description")
            if (values.containsKey("propertyId")) photo.propertyId = values.getAsInteger("propertyId")
            if (values.containsKey("isPrimaryPhoto")) photo.isPrimaryPhoto = values.getAsBoolean("isPrimaryPhoto")

            return photo
        }
    }
}