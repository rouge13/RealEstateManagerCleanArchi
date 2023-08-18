package com.openclassrooms.realestatemanager

import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedUtilsViewModel
import io.mockk.clearAllMocks
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@LooperMode(LooperMode.Mode.PAUSED)
class SharedUtilsViewModelTest {

    private lateinit var viewModel: SharedUtilsViewModel
    private val booleanObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
    private val dateFormatObserver: Observer<SimpleDateFormat> = mockk(relaxUnitFun = true)

    @Before
    fun setUp() {
        viewModel = SharedUtilsViewModel()
        clearMocks(booleanObserver, dateFormatObserver)
    }

    @Test
    fun testSetActiveSelectionMoneyRate() {
        // Set the money rate selected by the agent to Euros
        viewModel.getMoneyRateSelected.observeForever(booleanObserver)
        viewModel.setActiveSelectionMoneyRate(ACTIVE_SELECTION_EURO)

        // AssertTrue to check if the LiveData value is true
        assertTrue(viewModel.getMoneyRateSelected.value!!)

        // Set the money rate selected by the agent to Dollars
        viewModel.getMoneyRateSelected.observeForever(booleanObserver)
        viewModel.setActiveSelectionMoneyRate(ACTIVE_SELECTION_DOLLAR)

        // AssertTrue to check if the LiveData value is false
        assertTrue(!viewModel.getMoneyRateSelected.value!!)
    }

    @Test
    fun testSetDateFormatSelected() {
        // Set the date format selected by the agent to USA SimpleDateFormat
        viewModel.getDateFormatSelected.observeForever(dateFormatObserver)
        viewModel.setDateFormatSelected(dateUSAFormatSelected)

        // AssertTrue to check if the LiveData value is equal to the USA SimpleDateFormat we set
        assertTrue(viewModel.getDateFormatSelected.value == dateUSAFormatSelected)

        // Set the date format selected by the agent to France SimpleDateFormat
        viewModel.getDateFormatSelected.observeForever(dateFormatObserver)
        viewModel.setDateFormatSelected(dateEUFormatSelected)

        // AssertTrue to check if the LiveData value is equal to the France SimpleDateFormat we set
        assertTrue(viewModel.getDateFormatSelected.value == dateEUFormatSelected)
    }

    @After
    fun tearDown() {
        viewModel.getMoneyRateSelected.removeObserver(booleanObserver)
        viewModel.getDateFormatSelected.removeObserver(dateFormatObserver)
    }

    companion object {
        private val dateUSAFormatSelected = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        private val dateEUFormatSelected = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        private const val ACTIVE_SELECTION_EURO = true
        private const val ACTIVE_SELECTION_DOLLAR = false
    }
}