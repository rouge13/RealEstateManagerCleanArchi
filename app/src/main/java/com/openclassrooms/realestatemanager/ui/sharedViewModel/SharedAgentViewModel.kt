package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.ui.LocationLiveData
import com.openclassrooms.realestatemanager.ui.MainApplication
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
open class SharedAgentViewModel(private val repository: AgentRepository, application: MainApplication) : AndroidViewModel(
    application) {

    // Init location
    private val locationLiveData = application.applicationContext.let { LocationLiveData(it) }

    // Get agent location
    open fun getLocationLiveData() = locationLiveData

    // Start location update
    fun startLocationUpdates() {
        locationLiveData.startLocationUpdates()
    }

    // get all agents
    val allAgents: LiveData<List<AgentEntity>> = repository.allAgents.asLiveData()
    fun getAgentData(agentId: Int): LiveData<AgentEntity> {
        return repository.getAgentData(agentId).asLiveData()
    }

    // Insert agent
    suspend fun insertAgent(agent: AgentEntity): Long? {
        return repository.insert(agent)
    }

    // Get agent by name
    fun getAgentByName(agentName: String): Flow<AgentEntity?> {
        return repository.getAgentByName(agentName)
    }

    // Get agent has internet
    fun agentHasInternet() = repository.getAgentHasInternet()

    // On cleared unregister network callback to avoid memory leaks
    override fun onCleared() {
        super.onCleared()
        repository.cleanup()
    }
}