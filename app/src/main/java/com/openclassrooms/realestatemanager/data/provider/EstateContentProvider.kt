package com.openclassrooms.realestatemanager.data.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import com.openclassrooms.realestatemanager.data.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class EstateContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.openclassrooms.realestatemanager.data.provider"
        private val TABLE_NAME: String = PropertyEntity::class.java.simpleName
        val URI_PROPERTY: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
        private const val CODE_PROPERTY_DIR = 1
        private const val CODE_PROPERTY_ITEM = 2
        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH)

        init {
            MATCHER.addURI(
                AUTHORITY,
                TABLE_NAME,
                CODE_PROPERTY_DIR
            )
            MATCHER.addURI(
                AUTHORITY,
                "$TABLE_NAME/*",
                CODE_PROPERTY_ITEM
            )
        }
    }

    private lateinit var database: PropertyDatabase

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(): Boolean {
        GlobalScope.launch(Dispatchers.IO) {
            database = PropertyDatabase.getDatabase(context!!)
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val code = MATCHER.match(uri)
        return if (code == CODE_PROPERTY_DIR || code == CODE_PROPERTY_ITEM) {
            val context = context ?: return null
            val propertyDao: PropertyDao = database.propertyDao()
//            val cursor: Cursor = if (code == CODE_PROPERTY_DIR) {
//                propertyDao.getAllPropertiesForContentProvider()
//            } else {
            val cursor: Cursor = propertyDao.getPropertyByIdCursorReturned(ContentUris.parseId(uri))
//            }
            cursor.setNotificationUri(context.contentResolver, uri)
            cursor
        } else {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String {
        return when (MATCHER.match(uri)) {
            CODE_PROPERTY_DIR -> "vnd.android.cursor.dir/$AUTHORITY.$TABLE_NAME"
            CODE_PROPERTY_ITEM -> "vnd.android.cursor.item/$AUTHORITY.$TABLE_NAME"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        return when (MATCHER.match(uri)) {
            CODE_PROPERTY_DIR -> {
                val context = context ?: return null
                val property = PropertyEntity.fromContentValues(contentValues!!)
                val id = database.propertyDao().insertForContentProvider(property)
                Log.d("Insert", "ID: $id")
                context.contentResolver.notifyChange(uri, null)
                ContentUris.withAppendedId(uri, id)
            }
            CODE_PROPERTY_ITEM -> throw IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return when (MATCHER.match(uri)) {
            CODE_PROPERTY_DIR -> {
                val context = context ?: return 0
                val count: Int = database.propertyDao()
                    .deletePropertyForContentProvider(ContentUris.parseId(uri))
                context.contentResolver.notifyChange(uri, null)
                count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(uri: Uri, contentValues: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return when (MATCHER.match(uri)) {
            CODE_PROPERTY_ITEM -> throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            CODE_PROPERTY_DIR -> {
                val context = context ?: return 0
                val count: Int = database.propertyDao()
                    .updatePropertyForContentProvider(
                        PropertyEntity.fromContentValues(contentValues!!).apply {
                            id = ContentUris.parseId(uri).toInt()
                        }
                    )
                context.contentResolver.notifyChange(uri, null)
                count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}


