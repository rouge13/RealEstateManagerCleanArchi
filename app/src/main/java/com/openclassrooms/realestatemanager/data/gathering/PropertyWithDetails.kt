package com.openclassrooms.realestatemanager.data.gathering

import android.os.Parcelable
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import kotlinx.android.parcel.Parcelize

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Parcelize
data class PropertyWithDetails(
    val property: PropertyEntity?,
    val address: AddressEntity?,
    val photos: List<PhotoEntity>?
) : Parcelable