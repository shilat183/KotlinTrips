package com.trips.project.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Destination")
data class DestinationModel(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        var key: String? = null,
        var userEmail: String,
        var imageUrl1: String,
        var imageUrl2: String,
        var imageUrl3: String,
        var name: String,
        var price: String,
        var country_name: String,
        var flight_time: String,
        var flight_company: String,
        var trip_duration: String,
        var trip_review: String

) {

        constructor(
                key: String,
                userEmail: String,
                imageUrl1: String,
                imageUrl2: String,
                imageUrl3: String,
                name: String,
                price: String,
                country_name: String,
                flight_time: String,
                flight_company: String,
                trip_duration: String ,
                trip_review: String ,
        ) : this(0, key, userEmail, imageUrl1, imageUrl2, imageUrl3, name,
                price, country_name, flight_time, flight_company, trip_duration, trip_review)
}

