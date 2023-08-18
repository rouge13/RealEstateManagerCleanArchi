package com.openclassrooms.realestatemanager.data.database

import com.openclassrooms.realestatemanager.data.converter.Converters
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class FixturesData {
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        private val converters = Converters()
        val PROPERTY_PHOTO_LIST = listOf(
            PhotoEntity(1, "ic_billard_room", "Billiard Room",1),
            PhotoEntity(2, "ic_bathroom_3_house_1","Bathroom", 2),
            PhotoEntity(3, "ic_duplex_bathroom_1", "Bathroom",3),
            PhotoEntity(4, "ic_bathroom_4_house_1", "Bathroom",4),
            PhotoEntity(5, "ic_bedroom_1", "Bedroom",5),
            PhotoEntity(6, "ic_bedroom_2", "Bedroom",1),
            PhotoEntity(7, "ic_bedroom_3_house_1", "Bedroom",2),
            PhotoEntity(8, "ic_bedroom_4_house_1", "Bedroom",3),
            PhotoEntity(9, "ic_bedroom_1", "Bedroom",4),
            PhotoEntity(10, "ic_bedroom_2", "Bedroom",6),
            PhotoEntity(11, "ic_duplex_salon_1", "Living room",5),
            PhotoEntity(12, "ic_duplex_salon_1","Living room", 6),
            PhotoEntity(13, "ic_duplex_kitchen_1","Kitchen", 5),
            PhotoEntity(14, "ic_kitchen_1","Kitchen", 6),
            PhotoEntity(15, "ic_kitchen_3_house_1", "Kitchen",2),
            PhotoEntity(16, "ic_billard_room", "Billiard room",6)
        )

        val AGENT_LIST = listOf(
            AgentEntity(1, "Hammer Julien"),
            AgentEntity(2, "Smith Carl")
        )

        val PROPERTY_ADDRESS_LIST = listOf(
            AddressEntity(1,  "345", "Park Ave", "New York", "Manhattan", "10154", "USA",  1, "Apt 6/7 A"),
            AddressEntity(2, "160", "Schermerhorn St", "Brooklyn", "Brooklyn", "11201", "USA",  2),
            AddressEntity(3, "29-10", "Thomson Ave", "Long Island City", "Queens", "11101", "USA",  3, "Apt 12/5 A"),
            AddressEntity(4, "1", "Edgewater Plaza", "Staten Island", "Staten Island", "10305", "USA",  4, "Apt 1/2 A"),
            AddressEntity(5, "126-02", "82nd Ave", "Kew Gardens", "Queens", "11415", "USA",  5),
            AddressEntity(6, "174th", "St & Grand Concourse", "Bronx", "Bronx", "10457", "USA",  6)
        )

        val PROPERTY_LIST = listOf(
            PropertyEntity(
                1,
                17000000,
                250,
                10,
                2,
                2,
                "A flat, also known as an apartment, is a self-contained housing unit that occupies only part of a building. In the United States, flats are typically rented rather than owned, although it is possible to buy a flat in some areas. Flats can vary in size and layout, but they generally include a living area, one or more bedrooms, a bathroom, and a kitchen. Some flats may also have additional amenities, such as a balcony, a laundry room, or a swimming pool. The cost of renting a flat in the US can vary widely depending on the location, size, and quality of the unit.",
                "Flat",
                false,
                converters.dateToTimestamp(dateFormat.parse("2023/04/09")),
                null,
                1,
                "ic_flat_house1",
                false,
                true,
                false,
                true,
                false,
                1681057947516
            ),
            PropertyEntity(
                2,
                25000000,
                300,
                14,
                3,
                3,
                "A house typically refers to a physical structure that is used as a dwelling. It is often associated with the idea of property ownership and can be used to describe a variety of different types of residential buildings, such as single-family homes, townhouses, and apartments.",
                "House",
                false,
                converters.dateToTimestamp(dateFormat.parse("2023/04/10")),
                null,
                2,
                "ic_house_classic1",
                true,
                true,
                true,
                true,
                true,
                1681058041885
            ),
            PropertyEntity(
                3,
                300000000,
                3000,
                25,
                6,
                6,
                "In terms of design, duplex homes can vary widely depending on the preferences of the builder and the needs of the occupants. Some duplexes may be designed to look like a single-family home from the outside, while others may have a more modern or contemporary appearance. The interior layout of each unit may also differ, with some featuring open floor plans and others having more traditional room divisions.",
                "Duplex",
                false,
                converters.dateToTimestamp(dateFormat.parse("2023/04/12")),
                null,
                1,
                "ic_duplex_house1",
                false,
                false,
                false,
                false,
                false,
                1681058941044
            ),
            PropertyEntity(
                4,
                350000000,
                3500,
                22,
                8,
                6,
                "A penthouse is a luxurious apartment or condominium unit that is typically located on the top floor of a high-rise building. These types of homes are often associated with luxury living and can offer spectacular views of the surrounding area.",
                "Penthouse",
                false,
                converters.dateToTimestamp(dateFormat.parse("2023/04/15")),
                null,
                2,
                "ic_penthouse_house1",
                false,
                true,
                false,
                false,
                true,
                1681058511034
            ),
            PropertyEntity(
                5,
                23000000,
                320,
                14,
                4,
                4,
                "A house typically refers to a physical structure that is used as a dwelling. It is often associated with the idea of property ownership and can be used to describe a variety of different types of residential buildings, such as single-family homes, townhouses, and apartments.",
                "House",
                false,
                converters.dateToTimestamp(dateFormat.parse("2023/04/20")),
                null,
                1,
                "ic_house_classic2",
                false,
                false,
                true,
                false,
                false,
                1681058041885
            ),
            PropertyEntity(
                6,
                24000000,
                370,
                9,
                3,
                3,
                "A house typically refers to a physical structure that is used as a dwelling. It is often associated with the idea of property ownership and can be used to describe a variety of different types of residential buildings, such as single-family homes, townhouses, and apartments.",
                "House",
                true,
                converters.dateToTimestamp(dateFormat.parse("2023/04/22")),
                converters.dateToTimestamp(dateFormat.parse("2023/04/22")),
                1,
                "ic_house_classic3",
                true,
                false,
                false,
                true,
                true,
                1681058918711
            )
        )
    }
}