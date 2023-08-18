package com.openclassrooms.realestatemanager.data.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Dao
interface AgentDao {
    // Get all Agents
    @Query("SELECT * FROM agent ORDER BY id ASC")
    fun getAllAgents(): Flow<List<AgentEntity>>

    // Insert agent
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(agent: AgentEntity): Long

    // Insert agent for content provider
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAgentForContentProvider(agent: AgentEntity): Long

    // Get agent data to connect
    @Query("SELECT * FROM agent WHERE id = :agentId")
    fun getAgentData(agentId: Int): Flow<AgentEntity>

    // Get agent by name
    @Query("SELECT * FROM agent WHERE name = :agentName LIMIT 1")
    fun getAgentByName(agentName: String): Flow<AgentEntity?>

}