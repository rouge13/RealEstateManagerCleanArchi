package com.openclassrooms.realestatemanager.data.model

import android.content.ContentValues
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Parcelize
@Entity(
    tableName = "address", foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["propertyId"]
        )
    ],
    indices = [Index(value = ["propertyId"])]
)
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var streetNumber: String = "",
    var streetName: String = "",
    var city: String = "",
    var boroughs: String? = "",
    var zipCode: String = "",
    var country: String = "",
    var propertyId: Int? = null,
    var apartmentDetails: String? = "",
    var latitude: Double? = null,
    var longitude: Double? = null
) : Parcelable {
    companion object {
        // Getting content values for content provider from property entity
        fun fromContentValues(values: ContentValues): AddressEntity{
            val address = AddressEntity()
            if (values.containsKey("id")) address.id = values.getAsInteger("id")
            if (values.containsKey("streetNumber")) address.streetNumber = values.getAsString("streetNumber")
            if (values.containsKey("streetName")) address.streetName = values.getAsString("streetName")
            if (values.containsKey("city")) address.city = values.getAsString("city")
            if (values.containsKey("boroughs")) address.boroughs = values.getAsString("boroughs")
            if (values.containsKey("zipCode")) address.zipCode = values.getAsString("zipCode")
            if (values.containsKey("country")) address.country = values.getAsString("country")
            if (values.containsKey("propertyId")) address.propertyId = values.getAsInteger("propertyId")
            if (values.containsKey("apartmentDetails")) address.apartmentDetails = values.getAsString("apartmentDetails")
            if (values.containsKey("latitude")) address.latitude = values.getAsDouble("latitude")
            if (values.containsKey("longitude")) address.longitude = values.getAsDouble("longitude")
            return address
        }
    }
}