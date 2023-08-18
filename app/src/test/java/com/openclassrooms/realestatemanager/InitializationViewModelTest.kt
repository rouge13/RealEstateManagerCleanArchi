package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.viewmodel.InitializationViewModel
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.bouncycastle.util.test.SimpleTest.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class InitializationViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val mockApplication = mockk<MainApplication>(relaxed = true)

    private lateinit var initializationViewModel: InitializationViewModel

    @Before
    fun setup() {
        initializationViewModel = InitializationViewModel()
    }

    @Test
    fun startInitialization_callsWaitForInitialization() = runTest {
        initializationViewModel.startInitialization(mockApplication)
        coVerify{(mockApplication).waitForInitialization()}
    }

    @After
    fun cleanup() {
        testDispatcher.cleanupTestCoroutines()
    }
}