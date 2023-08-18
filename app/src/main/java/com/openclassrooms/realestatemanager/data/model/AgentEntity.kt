package com.openclassrooms.realestatemanager.data.model

import android.content.ContentValues
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
// Make the AgentEntity a Room entity
@Parcelize
@Entity(tableName = "agent")
data class AgentEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var name: String? = null
) : Parcelable {
    companion object {
        // Getting content values for content provider from property entity
        fun fromContentValues(values: ContentValues): AgentEntity{
            val agent = AgentEntity()
            if (values.containsKey("id")) agent.id = values.getAsInteger("id")
            if (values.containsKey("name")) agent.name = values.getAsString("name")
            return agent
        }
    }

}