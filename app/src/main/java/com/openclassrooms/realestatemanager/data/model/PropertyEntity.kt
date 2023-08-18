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
// Make the PropertyEntity a Room entity
@Parcelize
@Entity(
    tableName = "property",
    foreignKeys = [
        ForeignKey(
            entity = AgentEntity::class,
            parentColumns = ["id"],
            childColumns = ["agentId"],
        )
    ],
    indices = [Index(value = ["agentId"])]
)
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var price: Int? = 0,
    var squareFeet: Int? = 0,
    var roomsCount: Int? = 0,
    var bedroomsCount: Int? = 0,
    var bathroomsCount: Int? = 0,
    var description: String? = "Must add description for this property in later time",
    var typeOfHouse: String? = null,
    var isSold: Boolean? = false,
    var dateStartSelling: Long? = null,
    var dateSold: Long? = null,
    var agentId: Int? = null,
    var primaryPhoto: String? = null,
    var schoolProximity: Boolean? = null,
    var parkProximity: Boolean? = null,
    var shoppingProximity: Boolean? = null,
    var restaurantProximity: Boolean? = null,
    var publicTransportProximity: Boolean? = null,
    var lastUpdate: Long = System.currentTimeMillis()
) : Parcelable {


    companion object {
        // Getting content values for content provider from property entity
        fun fromContentValues(values: ContentValues): PropertyEntity{
            val property = PropertyEntity()
            if (values.containsKey("id")) property.id = values.getAsInteger("id")
            if (values.containsKey("price")) property.price = values.getAsInteger("price")
            if (values.containsKey("squareFeet")) property.squareFeet = values.getAsInteger("squareFeet")
            if (values.containsKey("roomsCount")) property.roomsCount = values.getAsInteger("roomsCount")
            if (values.containsKey("bedroomsCount")) property.bedroomsCount = values.getAsInteger("bedroomsCount")
            if (values.containsKey("bathroomsCount")) property.bathroomsCount = values.getAsInteger("bathroomsCount")
            if (values.containsKey("description")) property.description = values.getAsString("description")
            if (values.containsKey("typeOfHouse")) property.typeOfHouse = values.getAsString("typeOfHouse")
            if (values.containsKey("isSold")) property.isSold = values.getAsBoolean("isSold")
            if (values.containsKey("dateStartSelling")) property.dateStartSelling = values.getAsLong("dateStartSelling")
            if (values.containsKey("dateSold")) property.dateSold = values.getAsLong("dateSold")
            if (values.containsKey("agentId")) property.agentId = values.getAsInteger("agentId")
            if (values.containsKey("primaryPhoto")) property.primaryPhoto = values.getAsString("primaryPhoto")
            if (values.containsKey("schoolProximity")) property.schoolProximity = values.getAsBoolean("schoolProximity")
            if (values.containsKey("parkProximity")) property.parkProximity = values.getAsBoolean("parkProximity")
            if (values.containsKey("shoppingProximity")) property.shoppingProximity = values.getAsBoolean("shoppingProximity")
            if (values.containsKey("restaurantProximity")) property.restaurantProximity = values.getAsBoolean("restaurantProximity")
            if (values.containsKey("publicTransportProximity")) property.publicTransportProximity = values.getAsBoolean("publicTransportProximity")
            if (values.containsKey("lastUpdate")) property.lastUpdate = values.getAsLong("lastUpdate")
            return property
        }
    }
}