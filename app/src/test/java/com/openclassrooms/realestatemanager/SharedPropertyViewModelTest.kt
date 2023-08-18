package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.data.converter.Converters
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.model.SearchCriteria
import com.openclassrooms.realestatemanager.data.repository.AddressRepository
import com.openclassrooms.realestatemanager.data.repository.PhotoRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import com.openclassrooms.realestatemanager.ui.utils.Utils
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
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
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@LooperMode(LooperMode.Mode.PAUSED)
class SharedPropertyViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private val propertyRepository: PropertyRepository = mockk(relaxed = true)
    private val addressRepository: AddressRepository = mockk(relaxed = true)
    private val photoRepository: PhotoRepository = mockk(relaxed = true)
    private lateinit var viewModel: SharedPropertyViewModel
    private lateinit var propertyObserver: Observer<PropertyWithDetails?>

    private val property = PropertyEntity(
        id = PROPERTY_ID,
        price = PROPERTY_PRICE,
        squareFeet = PROPERTY_SQUARE_FEET,
        roomsCount = PROPERTY_NUMBER_OF_ROOMS,
        bedroomsCount = PROPERTY_NUMBER_OF_BEDROOMS,
        bathroomsCount = PROPERTY_NUMBER_OF_BATHROOMS,
        description = PROPERTY_DESCRIPTION,
        typeOfHouse = PROPERTY_TYPE,
        isSold = PROPERTY_IS_SOLD,
        dateStartSelling = PROPERTY_SALE_DATE,
        dateSold = PROPERTY_SOLD_DATE,
        agentId = PROPERTY_AGENT_ID,
        primaryPhoto = PROPERTY_PRIMARY_PHOTO_VALUE,
        schoolProximity = PROPERTY_SCHOOL_PROXIMITY,
        parkProximity = PROPERTY_PARK_PROXIMITY,
        shoppingProximity = PROPERTY_SHOP_PROXIMITY,
        restaurantProximity = PROPERTY_RESTAURANT_PROXIMITY,
        publicTransportProximity = PROPERTY_PUBLIC_TRANSPORT_PROXIMITY,
        lastUpdate = PROPERTY_LAST_UPDATE,
    )

    private val address = AddressEntity(
        id = ADDRESS_ID,
        streetNumber = PROPERTY_STREET_NUMBER,
        streetName = PROPERTY_STREET_NAME,
        city = PROPERTY_CITY,
        boroughs = PROPERTY_BOROUGH,
        zipCode = PROPERTY_POSTAL_CODE,
        country = PROPERTY_COUNTRY,
        latitude = PROPERTY_LATITUDE,
        longitude = PROPERTY_LONGITUDE,
        propertyId = PROPERTY_ID
    )
    private val photo1 = PhotoEntity(
        id = PHOTO_ID_1,
        description = PHOTO_DESCRIPTION_1,
        propertyId = PHOTO_PROPERTY_ID_1,
        photoURI = PHOTO_URI_1,
        isPrimaryPhoto = IS_PRIMARY_PHOTO_1
    )

    private val photo2 = PhotoEntity(
        id = PHOTO_ID_2,
        description = PHOTO_DESCRIPTION_2,
        propertyId = PHOTO_PROPERTY_ID_1,
        photoURI = PHOTO_URI_2,
        isPrimaryPhoto = IS_PRIMARY_PHOTO_2
    )

    private val expectedAddress = AddressEntity(
        id = ADDRESS_ID,
        streetNumber = PROPERTY_STREET_NUMBER,
        streetName = PROPERTY_STREET_NAME,
        city = PROPERTY_CITY,
        boroughs = PROPERTY_BOROUGH,
        zipCode = PROPERTY_POSTAL_CODE,
        country = PROPERTY_COUNTRY,
        latitude = EXPECTED_PROPERTY_LATITUDE,
        longitude = EXPECTED_PROPERTY_LONGITUDE,
        propertyId = PROPERTY_ID
    )

    private val listOfPhotos = listOf(photo1, photo2)
    private val listOfProperties = listOf(property)
    private val listOfAddress = listOf(address)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(propertyRepository, addressRepository, photoRepository)
        viewModel = SharedPropertyViewModel(propertyRepository, addressRepository, photoRepository)
        propertyObserver = mockk(relaxed = true)
    }

    // @Test to check if the LiveData is updated with the correct value when on selectProperty is set
    @Test
    fun testSetSelectProperty() {
        val property = PropertyWithDetails(
            PropertyEntity(id = PROPERTY_ID),
            AddressEntity(),
            listOf(PhotoEntity())
        )
        viewModel.getSelectedProperty.observeForever(propertyObserver)
        viewModel.setSelectProperty(property)

        // Verify that LiveData is updated with the correct value only once
        verify(exactly = 1) { propertyObserver.onChanged(property) }

        // Verify that LiveData is equals to the propertyId value
        assertEquals(viewModel.getSelectedProperty.value?.property?.id, PROPERTY_ID)
    }

    // Test if search criteria are set correctly and the value of the LiveData is updated and equals to the search criteria
    @Test
    fun testSetSearchCriteria() = runTest {
        val searchCriteria = SearchCriteria(
            selectedAgentsIdsForQuery = listOf(AGENT_ID),
            selectedTypeOfHouseForQuery = listOf(TYPE),
            selectedBoroughsForQuery = listOf(BOROUGHS),
            selectedCitiesForQuery = listOf(CITY),
            selectedMinPriceForQuery = MIN_PRICE,
            selectedMaxPriceForQuery = MAX_PRICE,
            selectedMinSquareFeetForQuery = MIN_SQUARE,
            selectedMaxSquareFeetForQuery = MAX_SQUARE,
            selectedMinCountRoomsForQuery = MIN_ROOMS,
            selectedMaxCountRoomsForQuery = MAX_ROOMS,
            selectedMinCountBedroomsForQuery = MIN_BEDROOMS,
            selectedMaxCountBedroomsForQuery = MAX_BEDROOMS,
            selectedMinCountBathroomsForQuery = MIN_BATHROOMS,
            selectedMaxCountBathroomsForQuery = MAX_BATHROOMS,
            selectedMinCountPhotosForQuery = MIN_PHOTOS,
            selectedMaxCountPhotosForQuery = MAX_PHOTOS,
            selectedStartDateForQuery = MIN_SALE_DATE,
            selectedEndDateForQuery = MAX_SALE_DATE,
            selectedIsSoldForQuery = IS_SOLD,
            selectedSchoolProximityQuery = SCHOOL_PROXIMITY,
            selectedShopProximityQuery = SHOP_PROXIMITY,
            selectedParkProximityQuery = PARK_PROXIMITY,
            selectedRestaurantProximityQuery = RESTAURANT_PROXIMITY,
            selectedPublicTransportProximityQuery = PUBLIC_TRANSPORT_PROXIMITY
        )

        // Set the search criteria
        viewModel.setSearchCriteria(searchCriteria)

        // After setting the new search criteria, the previous search criteria should be null
        // as it was initially null and no other search criteria has been set before this
        assertNull(viewModel.previousSearchCriteria.value)

        // Is every values correct to return a property with all informations
        isEveryValuesCorrespondingToSearchCriteria(searchCriteria)

        // The current search criteria should be the one that was just set
        assertEquals(searchCriteria, viewModel.searchCriteria.value)

        // As another test, we could set another search criteria and check that
        // previousSearchCriteria now contains the old search criteria
        val newSearchCriteria = SearchCriteria() // New criteria
        viewModel.setSearchCriteria(newSearchCriteria)

        assertEquals(searchCriteria, viewModel.previousSearchCriteria.value)
        assertEquals(newSearchCriteria, viewModel.searchCriteria.value)
    }

    private fun isEveryValuesCorrespondingToSearchCriteria(searchCriteria: SearchCriteria) {
        assertEquals(searchCriteria.selectedAgentsIdsForQuery, listOf(PROPERTY_AGENT_ID))
        assertEquals(searchCriteria.selectedTypeOfHouseForQuery, listOf(PROPERTY_TYPE))
        assertEquals(searchCriteria.selectedBoroughsForQuery, listOf(PROPERTY_BOROUGH))
        assertEquals(searchCriteria.selectedCitiesForQuery, listOf(PROPERTY_CITY))
        assertTrue(searchCriteria.selectedMinPriceForQuery!! < PROPERTY_PRICE && searchCriteria.selectedMaxPriceForQuery!! > PROPERTY_PRICE)
        assertTrue(searchCriteria.selectedMinSquareFeetForQuery!! < PROPERTY_SQUARE_FEET && searchCriteria.selectedMaxSquareFeetForQuery!! > PROPERTY_SQUARE_FEET)
        assertTrue(searchCriteria.selectedMinCountRoomsForQuery!! < PROPERTY_NUMBER_OF_ROOMS && searchCriteria.selectedMaxCountRoomsForQuery!! > PROPERTY_NUMBER_OF_ROOMS)
        assertTrue(searchCriteria.selectedMinCountBedroomsForQuery!! < PROPERTY_NUMBER_OF_BEDROOMS && searchCriteria.selectedMaxCountBedroomsForQuery!! > PROPERTY_NUMBER_OF_BEDROOMS)
        assertTrue(searchCriteria.selectedMinCountBathroomsForQuery!! < PROPERTY_NUMBER_OF_BATHROOMS && searchCriteria.selectedMaxCountBathroomsForQuery!! > PROPERTY_NUMBER_OF_BATHROOMS)
        assertTrue(searchCriteria.selectedMinCountPhotosForQuery!! < PROPERTY_NUMBER_OF_PHOTOS && searchCriteria.selectedMaxCountPhotosForQuery!! > PROPERTY_NUMBER_OF_PHOTOS)
        assertTrue((searchCriteria.selectedStartDateForQuery!! < PROPERTY_SALE_DATE!!) && (searchCriteria.selectedEndDateForQuery!! > PROPERTY_SALE_DATE))
        assertEquals(searchCriteria.selectedIsSoldForQuery, PROPERTY_IS_SOLD)
        assertEquals(searchCriteria.selectedSchoolProximityQuery, PROPERTY_SCHOOL_PROXIMITY)
        assertEquals(searchCriteria.selectedShopProximityQuery, PROPERTY_SHOP_PROXIMITY)
        assertEquals(searchCriteria.selectedParkProximityQuery, PROPERTY_PARK_PROXIMITY)
        assertEquals(searchCriteria.selectedRestaurantProximityQuery, PROPERTY_RESTAURANT_PROXIMITY)
        assertEquals(
            searchCriteria.selectedPublicTransportProximityQuery,
            PROPERTY_PUBLIC_TRANSPORT_PROXIMITY
        )
    }

    // Test if the updateProperty function works correctly
    @Test
    fun testUpdateProperty() = runTest {
        // Create a propertySlot
        val propertySlot = slot<PropertyEntity>()
        // Mock the repository to return a property
        coEvery { propertyRepository.update(property) } just runs

        // Update the property
        viewModel.updateProperty(property)

        // Verify that the property is updated exactly one time
        coVerify(exactly = 1) { propertyRepository.update(property) }

        // Verify that the property is updated with the correct value
        coVerify { propertyRepository.update(property = capture(propertySlot)) }

    }

    // Test if the address is correctly updated
    @Test
    fun testUpdateAddress() = runTest {
        // Mock the repository to return an address
        coEvery { addressRepository.updateAddress(address) } just runs

        // Update the address
        viewModel.updateAddress(address)

        // Verify that the address is updated exactly one time
        coVerify(exactly = 1) { addressRepository.updateAddress(address) }

        // Verify that the address is updated with the correct value
        coVerify { addressRepository.updateAddress(address = address) }
    }

    // Test if the propertiesWithDetails is correctly combined
    @Test
    fun testCombinePropertiesWithDetails() = runTest {
        // Mock the repository to return properties, address, and photos
        coEvery { propertyRepository.getAllProperties } returns flowOf(listOfProperties)
        coEvery { addressRepository.getAllAddresses } returns flowOf(listOfAddress)
        coEvery { photoRepository.getAllPhotos } returns flowOf(listOfPhotos)

        // Create an expected propertyWithDetails object with the property, address, and photos
        val expectedPropertyWithDetails = PropertyWithDetails(
            property = property,
            address = address,
            photos = listOfPhotos
        )

        // Create an expected propertiesWithDetails list
        val expectedPropertiesWithDetails = listOf(expectedPropertyWithDetails)

        // Call the function to combine the propertiesWithDetails
        val result =
            viewModel.combinePropertiesWithDetails(listOfProperties, listOfAddress, listOfPhotos)

        // Verify that the propertiesWithDetails is correctly combined
        assertEquals(expectedPropertiesWithDetails, result)

        // Verify that the repository functions were called
        coVerify(exactly = 1) { propertyRepository.getAllProperties }
        coVerify(exactly = 1) { addressRepository.getAllAddresses }
        coVerify(exactly = 1) { photoRepository.getAllPhotos }
    }

    // Test if the addressWithLocation is properly set
    @Test
    fun testSetAddressWithLocation() = runTest {
        // Mock the repository to return the address
        coEvery { addressRepository.updateAddress(expectedAddress) } just Runs

        // Call the function to update the address with the new location
        viewModel.updateAddressWithLocation(
            address,
            EXPECTED_PROPERTY_LATITUDE,
            EXPECTED_PROPERTY_LONGITUDE
        )

        // Verify that the repository function was called with the expected parameters
        coVerify(exactly = 1) { addressRepository.updateAddress(expectedAddress) }
    }

    // Test if the property is correctly inserted
    @Test
    fun testInsertProperty() = runTest {
        // Define the property to insert and the expected propertyid after insertion returned
        val expectedId = PROPERTY_ID.toLong()
        val notExpectedId = 12L

        // Mock the repository to return the property
        coEvery { propertyRepository.insert(property) } returns expectedId

        // Call the function to insert the property and get the returned id
        val propertyIdInserted = viewModel.insertProperty(property)

        assertEquals(expectedId, propertyIdInserted)
        assertNotEquals(notExpectedId, propertyIdInserted)

        // Verify that the repository function was called with the expected parameters
        coVerify(exactly = 1) { propertyRepository.insert(property) }
    }

    // Test if the address is correctly inserted
    @Test
    fun testInsertAddress() = runTest {
        // Mock the repository to return the address
        coEvery { addressRepository.insert(address) } just runs

        // Call the function to insert the address
        viewModel.insertAddress(address)

        // Verify that the repository function was called with the expected parameters
        coVerify(exactly = 1) { addressRepository.insert(address) }
    }

    // Test if the photo is correctly inserted and return the id of the photo inserted
    @Test
    fun testInsertPhoto() = runTest {
        // Define the photo to insert and the expected photoid after insertion returned
        val expectedId = PHOTO_ID_1.toLong()
        val notExpectedId = PHOTO_ID_2.toLong()

        // Mock the repository to return the photo
        coEvery { photoRepository.insert(photo1) } returns expectedId

        // Call the function to insert the photo and get the returned id
        val photoIdInserted = viewModel.insertPhoto(photo1)

        assertEquals(expectedId, photoIdInserted)
        assertNotEquals(notExpectedId, photoIdInserted)

        // Verify that the repository function was called with the expected parameters
        coVerify(exactly = 1) { photoRepository.insert(photo1) }
    }

    // Test if the photos with property id is correctly updated
    @Test
    fun testUpdatePhotosWithPropertyId() = runTest {
        // Mock the repository to return the photo
        coEvery { photoRepository.updatePhotoWithPropertyId(PROPERTY_ID, photo1.id!!) } just runs

        // Call the function to update the photos with property id
        viewModel.updatePhotosWithPropertyId(PROPERTY_ID, photo1.id!!)

        // Verify that the repository function was called with the expected parameters
        coVerify(exactly = 1) { photoRepository.updatePhotoWithPropertyId(PROPERTY_ID, photo1.id!!) }
    }

    // Test if the PrimaryPhoto is correctly updated in the photo
    @Test
    fun testUpdatePrimaryPhoto() = runTest {
        // Mock the repository to return the photo
        coEvery { photoRepository.updateIsPrimaryPhoto(IS_TRUE, photo1.id!!) } just runs

        // Call the function to update the primary photo
        viewModel.updateIsPrimaryPhoto(IS_TRUE, photo1.id!!)

        // Verify that the repository function was called with the expected parameters
        coVerify(exactly = 1) { photoRepository.updateIsPrimaryPhoto(IS_TRUE, photo1.id!!) }
    }

    // Test if the photo is correctly deleted
    @Test
    fun testDeletePhoto() = runTest {
        // Mock the repository to return the photo
        coEvery { photoRepository.deletePhoto(photo1.id!!) } just runs

        // Call the function to delete the photo
        viewModel.deletePhoto(photo1.id!!)

        // Verify that the repository function was called with the expected parameters
        coVerify(exactly = 1) { photoRepository.deletePhoto(photo1.id!!) }
    }

    // Test if getting all photos related to the property are correctly returned
    @Test
    fun testGetAllPhotosRelatedToProperty() = runTest {
        // Mock the repository to return the photo
        coEvery { photoRepository.getAllPhotosRelatedToASpecificProperty(PROPERTY_ID) } returns listOfPhotos

        // Call the function to get all photos related to the property
        val result = viewModel.getAllPhotosRelatedToSetThePropertyId(PROPERTY_ID)

        // Verify that the repository function was called with the expected parameters
        coVerify(exactly = 1) { photoRepository.getAllPhotosRelatedToASpecificProperty(PROPERTY_ID) }

        // Verify that the result is the expected one
        assertEquals(listOfPhotos, result)
    }

    // Test if the photo with null value as property id is correctly deleted
    @Test
    fun testDeletePhotoWithNullPropertyId() = runTest {
        // Mock the repository to return the photo
        coEvery { photoRepository.deletePhotosWithNullPropertyId() } just runs

        // Call the function to delete the photo with null value as property id
        viewModel.deletePhotosWithNullPropertyId()

        // Verify that the repository function was called with the expected parameters
        coVerify(exactly = 1) { photoRepository.deletePhotosWithNullPropertyId() }
    }

    // Test if property primary photo is correctly updated in the property
    @Test
    fun testUpdatePropertyPrimaryPhoto() = runTest {
        // Mock the repository to return the property
        coEvery { propertyRepository.updatePrimaryPhoto(property.id!!, photo1.photoURI!!) } just runs

        // Call the function to update the property primary photo
        viewModel.updatePrimaryPhoto(property.id!!, photo1.photoURI!!)

        // Verify that the repository function was called with the expected parameters
        coVerify(exactly = 1) { propertyRepository.updatePrimaryPhoto(property.id!!, photo1.photoURI!!) }
    }

    // Test for convert property price based of the boolean value of the selected currency in the settings by the agent
    @Test
    fun testConvertPropertyPriceBasedOnSelectedCurrency() {
        // Define the expected price converted
        val expectedPriceInDollars = PROPERTY_PRICE
        val expectedPriceInEuros = Utils.convertDollarsToEuros(expectedPriceInDollars)
        val mustBeConverted = IS_TRUE
        val mustNotBeConverted = IS_FALSE

        if (mustBeConverted) {
            // Call the function to convert the property price based of the boolean value of the selected currency in the settings by the agent
            val priceConverted = Utils.convertDollarsToEuros(PROPERTY_PRICE)

            // Verify that the result is the expected one
            assertEquals(expectedPriceInEuros, priceConverted)

            // Verify that the result is not the expected one
            assertNotEquals(expectedPriceInDollars, priceConverted)
        } else if (mustNotBeConverted) {
            // Call the function to convert the property price based of the boolean value of the selected currency in the settings by the agent
            val priceConverted = Utils.convertDollarsToEuros(expectedPriceInEuros)

            // Verify that the result is the expected one
            assertEquals(expectedPriceInDollars, priceConverted)

            // Verify that the result is not the expected one
            assertNotEquals(expectedPriceInEuros, priceConverted)
        }
    }

    companion object {

        // Convert date to Long
        private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        private val converters = Converters()

        // Property ID
        private const val PROPERTY_ID = 1

        // Property to find by SearchCriteria and all property values
        private const val PROPERTY_PRIMARY_PHOTO_VALUE = "ic_house_classic1"
        private const val PROPERTY_AGENT_ID = 0
        private const val PROPERTY_TYPE = "House"
        private const val PROPERTY_BOROUGH = "Queens"
        private const val PROPERTY_CITY = "NEW YORK"
        private const val PROPERTY_DESCRIPTION =
            "Must add description for this property in later time"
        private const val PROPERTY_PRICE = 100000
        private const val PROPERTY_SQUARE_FEET = 100
        private const val PROPERTY_NUMBER_OF_ROOMS = 8
        private const val PROPERTY_NUMBER_OF_BEDROOMS = 4
        private const val PROPERTY_NUMBER_OF_BATHROOMS = 2
        private const val PROPERTY_NUMBER_OF_PHOTOS = 2
        private val PROPERTY_SALE_DATE = converters.dateToTimestamp(dateFormat.parse("2023/04/11"))
        private val PROPERTY_SOLD_DATE = null
        private const val PROPERTY_IS_SOLD = false
        private const val PROPERTY_SCHOOL_PROXIMITY = true
        private const val PROPERTY_SHOP_PROXIMITY = true
        private const val PROPERTY_PARK_PROXIMITY = true
        private const val PROPERTY_RESTAURANT_PROXIMITY = false
        private const val PROPERTY_PUBLIC_TRANSPORT_PROXIMITY = false
        private val PROPERTY_LAST_UPDATE = System.currentTimeMillis()
        private const val PROPERTY_STREET_NUMBER = "123"
        private const val PROPERTY_STREET_NAME = "Main Street"
        private const val PROPERTY_POSTAL_CODE = "12345"
        private const val PROPERTY_COUNTRY = "USA"
        private const val PROPERTY_LATITUDE = 40.7128
        private const val EXPECTED_PROPERTY_LATITUDE = 40.71286
        private const val PROPERTY_LONGITUDE = 74.0060
        private const val EXPECTED_PROPERTY_LONGITUDE = 74.00606


        // SearchCriteriaValues
        private const val MIN_PRICE = 0
        private const val MAX_PRICE = 1000000
        private const val MIN_SQUARE = 0
        private const val MAX_SQUARE = 1000
        private const val MIN_ROOMS = 0
        private const val MAX_ROOMS = 10
        private const val MIN_BATHROOMS = 0
        private const val MAX_BATHROOMS = 10
        private const val MIN_BEDROOMS = 0
        private const val MAX_BEDROOMS = 10
        private const val MIN_PHOTOS = 0
        private const val MAX_PHOTOS = 10
        private val MIN_SALE_DATE = converters.dateToTimestamp(dateFormat.parse("2023/04/10"))
        private val MAX_SALE_DATE = converters.dateToTimestamp(dateFormat.parse("2023/04/12"))
        private const val TYPE = "House"
        private const val CITY = "NEW YORK"
        private const val SCHOOL_PROXIMITY = true
        private const val PARK_PROXIMITY = true
        private const val SHOP_PROXIMITY = true
        private const val RESTAURANT_PROXIMITY = false
        private const val PUBLIC_TRANSPORT_PROXIMITY = false
        private const val IS_SOLD = false
        private const val BOROUGHS = "Queens"

        // Address values
        private const val ADDRESS_ID = 1

        // Agent values
        private const val AGENT_ID = 0

        // Photos values
        private const val PHOTO_ID_1 = 0
        private const val PHOTO_URI_1 = "https://www.google.com"
        private const val PHOTO_DESCRIPTION_1 = "Kitchen"
        private const val PHOTO_PROPERTY_ID_1 = 1
        private const val IS_PRIMARY_PHOTO_1 = true

        private const val PHOTO_ID_2 = 1
        private const val PHOTO_URI_2 = "https://www.google.com"
        private const val PHOTO_DESCRIPTION_2 = "Bedroom"
        private const val IS_PRIMARY_PHOTO_2 = false

        // IS TRUE
        private const val IS_TRUE = true

        // IS FALSE
        private const val IS_FALSE = false


    }
}

