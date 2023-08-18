package com.openclassrooms.realestatemanager

import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@LooperMode(LooperMode.Mode.PAUSED)
class SharedNavigationViewModelTest {
    private lateinit var viewModel: SharedNavigationViewModel
    private val booleanObserver: Observer<Boolean> = mockk(relaxUnitFun = true)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = SharedNavigationViewModel()
        clearMocks(booleanObserver)
    }

    @Test
    fun testSearchClickedLiveData() {
        // Setting LiveData value to false to doneNavigatingToSearch() method
        viewModel.searchClicked.observeForever(booleanObserver)
        viewModel.doneNavigatingToSearch()

        // Verifying LiveData value with the false value we set
        verify { booleanObserver.onChanged(DONE_NAVIGATING_TO_SEARCH) }

        // AssertFalse to check if the LiveData value is false
        assertEquals(viewModel.searchClicked.value, DONE_NAVIGATING_TO_SEARCH)
    }

    @Test
    fun testAddOrModifyClickedLiveData() {
        // Setting LiveData value to true to setAddOrModifyClicked() method for modification
        viewModel.getAddOrModifyClicked.observeForever(booleanObserver)
        viewModel.setAddOrModifyClicked(IS_MODIFY)

        // AssertTrue to check if the LiveData value is true
        assertEquals(viewModel.getAddOrModifyClicked.value, IS_MODIFY)

        // Setting LiveData value to false to setAddOrModifyClicked() method for addition
        viewModel.setAddOrModifyClicked(IS_ADD)

        // AssertFalse to check if the LiveData value is false
        assertEquals(viewModel.getAddOrModifyClicked.value, IS_ADD)
    }

    @Test
    fun testOnlineClickedLiveData() {
        // Setting LiveData value to true to setOnlineNavigation() method for online
        viewModel.getOnlineClicked.observeForever(booleanObserver)
        viewModel.setOnlineNavigation(IS_ONLINE)

        // AssertTrue to check if the LiveData value is true
        assertEquals(viewModel.getOnlineClicked.value, IS_ONLINE)

        // Setting LiveData value to false to setOnlineNavigation() method for offline
        viewModel.setOnlineNavigation(IS_OFFLINE)

        // AssertFalse to check if the LiveData value is false
        assertEquals(viewModel.getOnlineClicked.value, IS_OFFLINE)
    }

    @After
    fun tearDown() {
        viewModel.searchClicked.removeObserver(booleanObserver)
        viewModel.getAddOrModifyClicked.removeObserver(booleanObserver)
        viewModel.getOnlineClicked.removeObserver(booleanObserver)
    }

    companion object {
        private const val IS_ONLINE = true
        private const val IS_OFFLINE = false
        private const val IS_MODIFY = true
        private const val IS_ADD = false
        private const val DONE_NAVIGATING_TO_SEARCH = false
    }
}