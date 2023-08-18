package com.openclassrooms.realestatemanager.data.converter

import androidx.room.TypeConverter
import java.util.Date

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class Converters {

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

}