package com.openclassrooms.realestatemanager

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.openclassrooms.realestatemanager.data.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import com.openclassrooms.realestatemanager.data.provider.EstateContentProvider
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */

@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class EstateContentProviderTest {

    private lateinit var contentResolver: ContentResolver
    private lateinit var propertyDao: PropertyDao
    private lateinit var cursor: Cursor

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        contentResolver = context.contentResolver

        val database = Room.inMemoryDatabaseBuilder(
            context, PropertyDatabase::class.java
        ).allowMainThreadQueries().build()
        cursor = mockk(relaxed = true)
        propertyDao = database.propertyDao()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testInsertAndGet() {
        runTest {
            val uri = contentResolver.insert(EstateContentProvider.URI_PROPERTY, generateProperty())

            advanceUntilIdle()

            coEvery { cursor.count } returns 1
            assertThat(cursor).isNotNull()

            assertThat(cursor.count).isEqualTo(1)

            coEvery { cursor.getString(cursor.getColumnIndexOrThrow("primaryPhoto")) } returns "https://example.com/photo.jpg"

            assertEquals("https://example.com/photo.jpg", cursor.getString(cursor.getColumnIndexOrThrow("primaryPhoto")))
        }
    }

    @Test
    fun testDelete() {
        val uri = contentResolver.insert(EstateContentProvider.URI_PROPERTY, generateProperty())

        val deletedCount = contentResolver.delete(uri!!, null, null)

        assertEquals(1, deletedCount)
    }

    @Test
    fun testUpdate() {

        val uri = contentResolver.insert(EstateContentProvider.URI_PROPERTY, generateProperty())
        val updatedCount = contentResolver.update(uri!!, generateProperty(), null, null)

        assertEquals(1, updatedCount)
    }

    private fun generateProperty(): ContentValues {
        val values = ContentValues()
        values.put("id", 1)
        values.put("price", 250000)
        values.put("squareFeet", 1500)
        values.put("roomsCount", 5)
        values.put("bedroomsCount", 3)
        values.put("bathroomsCount", 2)
        values.put("description", "Beautiful property with great views.")
        values.put("typeOfHouse", "Detached")
        values.put("isSold", true)
        values.put("dateStartSelling", System.currentTimeMillis())
        values.put("dateSold", System.currentTimeMillis())
        values.put("agentId", 1)
        values.put("primaryPhoto", "https://example.com/photo.jpg")
        values.put("schoolProximity", true)
        values.put("parkProximity", true)
        values.put("shoppingProximity", false)
        values.put("restaurantProximity", true)
        values.put("publicTransportProximity", false)
        values.put("lastUpdate", System.currentTimeMillis())
        return values
    }
}