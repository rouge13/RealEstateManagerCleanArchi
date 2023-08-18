package com.openclassrooms.realestatemanager.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.data.dao.AgentDao
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AgentRepository(private val agentDao: AgentDao, private val context: Context) {
    // Add a callback to get isInternetAvailable value
    val isInternetAvailable: MutableLiveData<Boolean> = MutableLiveData()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // Called when network is available and connected
        override fun onAvailable(network: Network) {
            isInternetAvailable.postValue(true)
        }
        // Called when network is lost or disconnected
        override fun onLost(network: Network) {
            isInternetAvailable.postValue(false)
        }
        // Called when network is available but not connected
        override fun onUnavailable() {
            isInternetAvailable.postValue(false)
        }
    }
    // register the callback when you're done to avoid memory leaks
    init {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder()
            .addTransportType(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(android.net.NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    // make sure to unregister the callback when you're done to avoid memory leaks
    fun cleanup() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    // get all the agents from the database
    val allAgents: Flow<List<AgentEntity>> = agentDao.getAllAgents()

    // insert an agent in the database
    suspend fun insert(agent: AgentEntity): Long? {
        val id = agentDao.insert(agent)
        return if (id != -1L) id else null
    }

    // get agent by id
    fun getAgentData(agentId: Int): Flow<AgentEntity> {
        return agentDao.getAgentData(agentId)
    }

    // get agent by name
    fun getAgentByName(agentName: String): Flow<AgentEntity?> {
        return agentDao.getAgentByName(agentName)
    }

    // get agent has internet then online sync
    fun getAgentHasInternet(): LiveData<Boolean> {
        return isInternetAvailable
    }

}


