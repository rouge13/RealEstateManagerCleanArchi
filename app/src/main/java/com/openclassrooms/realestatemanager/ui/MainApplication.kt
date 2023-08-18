package com.openclassrooms.realestatemanager.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import com.openclassrooms.realestatemanager.data.repository.AddressRepository
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.data.repository.PhotoRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class MainApplication : Application() {
    // Add a mutex to avoid multiple threads to initialize the database at the same time (which would cause a crash) and a boolean to know if the database is initialized
    private val mutex = Mutex()
    var initialized = false
    // Add a coroutine scope to launch the database initialization in a background thread and a context to pass to the AgentRepository
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val context = this

    suspend fun waitForInitialization() {
        // If the database is not initialized, initialize it and wait for the job to complete before returning (to avoid a crash)
        if (!initialized) {
            mutex.withLock {
                if (!initialized) {
                    // Wait for the initialization job to complete
                    initJob.join()
                    initialized = true
                }
            }
        }
    }

    // Add database for the repositories to use it to access the DAOs and initialize it in a coroutine scope to avoid blocking the UI thread (and avoid a crash) and to avoid multiple threads to initialize the database at the same time (which would cause a crash)
    private var database: PropertyDatabase? = null

    // Add getters for the repositories
    val propertyRepository: PropertyRepository?
        get() = database?.propertyDao()?.let { PropertyRepository(it) }
    val agentRepository: AgentRepository?
        get() = database?.agentDao()?.let { AgentRepository(it, context) }
    val addressRepository: AddressRepository?
        get() = database?.addressDao()?.let { AddressRepository(it) }
    val photoRepository: PhotoRepository?
        get() = database?.photoDao()?.let { PhotoRepository(it) }

    // Init the job to initialize the database in a coroutine scope to avoid blocking the UI thread (and avoid a crash)
    private val initJob = applicationScope.launch {
        database = PropertyDatabase.getDatabase(this@MainApplication)
    }

    // Override onTerminate to cancel the coroutine scope when the application is terminated
    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }
}
