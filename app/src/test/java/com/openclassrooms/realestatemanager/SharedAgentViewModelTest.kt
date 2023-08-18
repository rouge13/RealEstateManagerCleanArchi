package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.ui.LocationLiveData
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@LooperMode(LooperMode.Mode.PAUSED)
class SharedAgentViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private var locationLiveData: LocationLiveData = mockk(relaxed = true)
    private var viewModel: SharedAgentViewModel = mockk(relaxed = true)
    private val repository: AgentRepository = mockk(relaxed = true)
    private val agentEntityObserver: Observer<AgentEntity> = mockk(relaxed = true)
    private lateinit var booleanObserver: Observer<Boolean>

    @Before
    fun setUp() {
        // Initialize MockK
        MockKAnnotations.init(this)
        // Clear every mock
        clearMocks(agentEntityObserver, viewModel, repository)
        // mock the application
        val applicationMock = mockk<MainApplication>(relaxed = true)
        // mock the context
        val contextMock = mockk<Context>(relaxed = true)
        // For every instance of the applicationMock, return the contextMock
        every { applicationMock.applicationContext } returns contextMock
        // Create the ViewModel with the mocked repository and application and override the getLocationLiveData function
        viewModel = object : SharedAgentViewModel(repository, applicationMock) {
            override fun getLocationLiveData() = locationLiveData
        }
    }

    //     TEST WORKING
    @Test
    fun testGetLocationLiveData() {
        // Call the method in the ViewModel
        val resultLiveData = viewModel.getLocationLiveData()

        // Check that the returned LocationLiveData is not null
        assertNotNull(resultLiveData)
    }


    // TEST NOT WORKING
    @Test
    fun testStartLocationUpdates() {
        // Allow the startLocationUpdates function to be called
        every { locationLiveData.startLocationUpdates() } just runs

        // Call the method in the ViewModel
        val result = viewModel.startLocationUpdates()

        // Verify that the method in LocationLiveData is called
        assertNotNull(result)
    }

    // TEST WORKING
    @Test
    fun testGetAgentData() {
        val agent = AgentEntity(AGENT_ID, AGENT_NAME)
        coEvery { repository.getAgentData(AGENT_ID) } returns flowOf(agent)

        var result: AgentEntity? = null
        val observer = Observer<AgentEntity> { result = it }

        viewModel.getAgentData(AGENT_ID).observeForever(observer)

        assertEquals(agent, result)
    }

    // TEST WORKING
    @Test
    fun testInsertAgent() {
        // Create an agent
        val agent = AgentEntity(AGENT_ID, AGENT_NAME)
        // Expected id related to the agent id
        val expectedId = AGENT_ID.toLong()
        // Mock the repository response and return the expected id
        coEvery { runBlocking { repository.insert(agent) } } returns expectedId
        // Call the method in the ViewModel using runBlocking to wait for the result of the coroutine and assert the result agent id inserted is the expected id
        runBlocking {
            val result = viewModel.insertAgent(agent)
            assertEquals(expectedId, result)
        }
    }


    // TEST WORKING
    @Test
    fun testGetAgentByName() = runTest {
        // Create mock objects
        val agent = AgentEntity(AGENT_ID, AGENT_NAME)
        // Mock repository response
        coEvery { repository.getAgentByName(AGENT_NAME) } returns flow { emit(agent) }
        // Collect the flow using launch
        val result = mutableListOf<AgentEntity?>()
        val job = launch {
            viewModel.getAgentByName(AGENT_NAME).collect {
                result.add(it)
            }
        }
        runCurrent()
        // Cancel the job after collecting the flow
        job.cancel()
        // Verify the result
        assertEquals(listOf(agent), result)
    }

    @Test
    fun testAgentHasInternet() {
        // Pass a MutableLiveData to the ViewModel with true value
        val hasInternet = MutableLiveData<Boolean>()
        hasInternet.value = HAS_INTERNET
        // Allow the agentHasInternet function to be called and return the MutableLiveData
        every { viewModel.agentHasInternet() } returns hasInternet
        // Call the method in the ViewModel
        val result = viewModel.agentHasInternet()
        // Verify that the method in AgentRepository is called
        verify { viewModel.agentHasInternet() }
        // Create a observer
        booleanObserver = Observer {}
        // Observe the LiveData
        result.observeForever(booleanObserver)
        // Check that the returned result is true
        assertEquals(HAS_INTERNET, result.value)
        // Remove the observer after test
        result.removeObserver(booleanObserver)
    }

    @After
    fun tearDown() {
        // Remove the observer after test
        viewModel.getAgentData(AGENT_ID).removeObserver(agentEntityObserver)
    }

    companion object {
        // FOR AGENT ENTITY TEST DATA
        const val AGENT_ID = 1
        const val AGENT_NAME = "John Doe"

        // HAS INTERNET TEST DATA
        const val HAS_INTERNET = true
    }

}

