package com.trips.project.data.model;

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.trips.project.data.db.DestinationDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DestinationViewModel(private val destinationDao: DestinationDao) : ViewModel() {
    val allDestinations: LiveData<List<DestinationModel>> = destinationDao.getAll()

    fun fetchDestinationsFromFirestore(userEmail: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("Destinations").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val destinations = mutableListOf<DestinationModel>() // List of destinations to be fetched

                    for (document in task.result!!) {
                        val imageUrl1 = document.getString("imageUrl1")
                        val imageUrl2 = document.getString("imageUrl2")
                        val imageUrl3 = document.getString("imageUrl3")
                        val destination = DestinationModel(
                            key = document.id,
                            userEmail = document.getString("userEmail") ?: "",
                            imageUrl1 = imageUrl1 ?: "",
                            imageUrl2 = imageUrl2 ?: "",
                            imageUrl3 = imageUrl3 ?: "",
                            name = document.getString("name") ?: "",
                            price = document.getString("price") ?: "",
                            country_name = document.getString("country_name") ?: "",
                            flight_time =document.getString("flight_time") ?: "",
                            flight_company = document.getString("flight_company") ?: "",
                            trip_duration = document.getString("trip_duration") ?: "",
                            trip_review = document.getString("trip_review") ?: ""
                        )
                        destinations.add(destination)
                    }

                    // Insert destinations into database within IO scope
                    CoroutineScope(Dispatchers.IO).launch {
                        destinationDao.deleteAll()
                        destinationDao.insertAll(destinations)
                    }.invokeOnCompletion {
                        // Do something after insertion completes if needed
                    }
                } else {
                    Log.e("DestinationViewModel", "Error fetching destinations")
                }
            }
        }
    }

    fun fetchMyDestinationsFromFirestore(userEmail: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("Destinations").whereEqualTo("userEmail", userEmail).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val destinations = mutableListOf<DestinationModel>() // List of destinations to be fetched

                    for (document in task.result!!) {
                        val imageUrl1 = document.getString("imageUrl1")
                        val imageUrl2 = document.getString("imageUrl2")
                        val imageUrl3 = document.getString("imageUrl3")
                        val destination = DestinationModel(
                            key = document.id,
                            userEmail = document.getString("userEmail") ?: "",
                            imageUrl1 = imageUrl1 ?: "",
                            imageUrl2 = imageUrl2 ?: "",
                            imageUrl3 = imageUrl3 ?: "",
                            name = document.getString("name") ?: "",
                            price = document.getString("price") ?: "",
                            country_name = document.getString("country_name") ?: "",
                            flight_time =document.getString("flight_time") ?: "",
                            flight_company = document.getString("flight_company") ?: "",
                            trip_duration = document.getString("trip_duration") ?: "",
                            trip_review = document.getString("trip_review") ?: ""
                        )
                        destinations.add(destination)
                    }

                    // Insert destinations into database within IO scope
                    CoroutineScope(Dispatchers.IO).launch {
                        destinationDao.deleteAll()
                        destinationDao.insertAll(destinations)
                    }.invokeOnCompletion {
                        // Do something after insertion completes if needed
                    }
                } else {
                    Log.e("DestinationViewModel", "Error fetching destinations")
                }
            }
        }
    }

    fun fetchFavouriteDestinationsForUserEmail(userEmail: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("Destinations").whereEqualTo("favourite", true) .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val destinations = mutableListOf<DestinationModel>() // List of destinations to be fetched

                    for (document in task.result!!) {
                        val imageUrl1 = document.getString("imageUrl1")
                        val imageUrl2 = document.getString("imageUrl2")
                        val imageUrl3 = document.getString("imageUrl3")
                        val destination = DestinationModel(
                            key = document.id,
                            userEmail = document.getString("userEmail") ?: "",
                            imageUrl1 = imageUrl1 ?: "",
                            imageUrl2 = imageUrl2 ?: "",
                            imageUrl3 = imageUrl3 ?: "",
                            name = document.getString("name") ?: "",
                            price = document.getString("price") ?: "",
                            country_name = document.getString("country_name") ?: "",
                            flight_time =document.getString("flight_time") ?: "",
                            flight_company = document.getString("flight_company") ?: "",
                            trip_duration = document.getString("trip_duration") ?: "",
                            trip_review = document.getString("trip_review") ?: ""
                        )
                        destinations.add(destination)
                    }

                    // Insert destinations into database within IO scope
                    CoroutineScope(Dispatchers.IO).launch {
                        destinationDao.deleteAll()
                        destinationDao.insertAll(destinations)
                    }.invokeOnCompletion {
                        // Do something after insertion completes if needed
                    }
                } else {
                    Log.e("DestinationViewModel", "Error fetching destinations for user email: $userEmail")
                }
            }
        }
    }
}
