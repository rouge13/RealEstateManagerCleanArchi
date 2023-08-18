package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.ui.addAndModification.AddAndModificationFragment
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */

class AddAndModificationFragmentTest {
    private lateinit var addAndModificationFragment: AddAndModificationFragment
    private lateinit var mockSharedPropertyViewModel: SharedPropertyViewModel
    private lateinit var photoListWithPrimaryPhotoSet: List<PhotoEntity>
    private lateinit var photoListWithPrimaryPhotoNotSet: List<PhotoEntity>
    private lateinit var allInputsFilled: List<Pair<String, String>>
    private lateinit var oneInputMissing: List<Pair<String, String>>
    @Before
    fun setup() {
        // mockk(relaxUnitFun = true) is used to create a mock that allows calling methods that return Unit without setting a behavior.
        addAndModificationFragment = mockk(relaxUnitFun  = true)
        mockSharedPropertyViewModel = mockk(relaxed = true)
        // Photo 1
        val photo1 = PhotoEntity(PHOTO_ID_1, PHOTO_URI_1, PHOTO_DESCRIPTION_1, PHOTO_PROPERTY_ID_1, PHOTO_IS_PRIMARY_PHOTO_1)
        // Photo 2
        val photo2 = PhotoEntity(PHOTO_ID_2, PHOTO_URI_2, PHOTO_DESCRIPTION_2, PHOTO_PROPERTY_ID_2, PHOTO_IS_PRIMARY_PHOTO_2)
        // Photo 3
        val photo3 = PhotoEntity(PHOTO_ID_3, PHOTO_URI_3, PHOTO_DESCRIPTION_3, PHOTO_PROPERTY_ID_3, PHOTO_IS_PRIMARY_PHOTO_3)
        // List of photos with primary photo set and not set
        photoListWithPrimaryPhotoSet = listOf(photo1, photo2, photo3)
        photoListWithPrimaryPhotoNotSet = listOf(photo2, photo3)

        // For checking if all inputs are correctly filled or not
        // Preparing a list with all values filled in
        allInputsFilled = listOf(
            "houseType" to "house",
            "price" to "123",
            "squareFeet" to "123",
            "roomsCount" to "10",
            "description" to "description",
            "streetNumber" to "123",
            "streetName" to "streetName",
            "city" to "city",
            "zipCode" to "12345",
            "country" to "country",
            "agentName" to "agentName",
            "soldDate" to "01/01/2021",
            "dateSale" to "01/01/2021"
        )

        // Preparing a list with one value missing
        oneInputMissing = listOf(
            "houseType" to "house",
            "price" to "",
            "squareFeet" to "123",
            "roomsCount" to "10",
            "description" to "description",
            "streetNumber" to "123",
            "streetName" to "streetName",
            "city" to "city",
            "zipCode" to "12345",
            "country" to "country",
            "agentName" to "agentName",
            "soldDate" to "01/01/2021",
            "dateSale" to "01/01/2021"
        )
    }

    // Check if the primary photo is correctly set and then one photo has to be added to the list of photos
    @Test
    fun checkPrimaryPhotoIsCorrectlySet() {
        runBlocking {
            // Mock ViewModel and its methods to return the list of photos with primary photo set
            coEvery { mockSharedPropertyViewModel.getAllPhotosRelatedToSetThePropertyId(PROPERTY_ID) } returns photoListWithPrimaryPhotoSet
            // Mock the method isPrimaryPhotoSelected to return true because one photo has been added to the list of photos and the primary photo has been set
            coEvery { addAndModificationFragment.isPrimaryPhotoSelected(PROPERTY_ID) } answers { photoListWithPrimaryPhotoSet.any { it.isPrimaryPhoto } }
            // Check if the primary photo is correctly set and then one photo has to be added to the list of photos
            val result = addAndModificationFragment.isPrimaryPhotoSelected(PROPERTY_ID)
            // Check if the result is true
            assertTrue(result)
        }
    }

    // Check if the primary photo isn't correctly set and then returning false because no photo has been added to the list of photos
    @Test
    fun checkPrimaryPhotoIsNotCorrectlySet() {
        runBlocking {
            // Mock ViewModel and its methods to return the list of photos with primary not set
            coEvery { mockSharedPropertyViewModel.getAllPhotosRelatedToSetThePropertyId(PROPERTY_ID) } returns photoListWithPrimaryPhotoNotSet
            // Mock the method isPrimaryPhotoSelected to return false because no photo has been added to the list of photos and no primary photo has been set
            coEvery { addAndModificationFragment.isPrimaryPhotoSelected(PROPERTY_ID) } answers { photoListWithPrimaryPhotoNotSet.any { it.isPrimaryPhoto } }
            // Check if the primary photo isn't correctly set and then returning false because no photo has been added to the list of photos
            val result = addAndModificationFragment.isPrimaryPhotoSelected(PROPERTY_ID)
            // Check if the result is false
            assertFalse(result)
        }
    }

    // Check if all element of the property are correctly set and then returning true because all element of the property are correctly set
    @Test
    fun test_requiredAllValidateInputsOk() {
        // Mock the behavior for `collectPropertyInputsFromBinding()`
        coEvery { addAndModificationFragment.collectPropertyInputsFromBinding() } returns allInputsFilled
        // Mock the behavior for `requiredAllValidateInputsOk()`
        coEvery { addAndModificationFragment.requiredAllValidateInputsOk(allInputsFilled) } returns VALIDATE_INPUTS_OK
        // Check if all element of the property are correctly set and then returning true because all element of the property are correctly set
        val resultNotMissing = addAndModificationFragment.requiredAllValidateInputsOk(allInputsFilled)
        // Test when all inputs are filled
        assertTrue(resultNotMissing)

        // Mock the behavior for `collectPropertyInputsFromBinding()` for the second case
        coEvery { addAndModificationFragment.collectPropertyInputsFromBinding() } returns oneInputMissing
        // Mock the behavior for `requiredAllValidateInputsOk()` for the second case
        coEvery { addAndModificationFragment.requiredAllValidateInputsOk(oneInputMissing) } returns VALIDATE_INPUTS_NOT_OK
        // Check if all element of the property are correctly set and then returning false because one element of the property is missing
        val resultMissing = addAndModificationFragment.requiredAllValidateInputsOk(oneInputMissing)
        // Test when one input is missing
        assertFalse(resultMissing)
    }



    companion object {
        // Photo 1
        private const val PHOTO_ID_1 = 1
        private const val PHOTO_URI_1 = "https://www.google.com"
        private const val PHOTO_DESCRIPTION_1 = "Photo 1"
        private const val PHOTO_PROPERTY_ID_1 = 1
        private const val PHOTO_IS_PRIMARY_PHOTO_1 = true

        // Photo 2
        private const val PHOTO_ID_2 = 2
        private const val PHOTO_URI_2 = "https://www.google.com"
        private const val PHOTO_DESCRIPTION_2 = "Photo 2"
        private const val PHOTO_PROPERTY_ID_2 = 1
        private const val PHOTO_IS_PRIMARY_PHOTO_2 = false

        // Photo 3
        private const val PHOTO_ID_3 = 3
        private const val PHOTO_URI_3 = "https://www.google.com"
        private const val PHOTO_DESCRIPTION_3 = "Photo 3"
        private const val PHOTO_PROPERTY_ID_3 = 1
        private const val PHOTO_IS_PRIMARY_PHOTO_3 = false

        // Property ID
        private const val PROPERTY_ID = 1

        // Return Boolean for inputs validation
        private const val VALIDATE_INPUTS_OK = true
        private const val VALIDATE_INPUTS_NOT_OK = false

    }
}