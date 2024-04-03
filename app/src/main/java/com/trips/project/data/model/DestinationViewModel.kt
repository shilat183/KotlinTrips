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
                        val imageUrl = document.getString("imageUrl")

                        val destination = DestinationModel(
                            key = document.id,
                            userEmail = document.getString("userEmail") ?: "",
                            imageUrl = imageUrl ?: "",
                            menu = document.getString("menu") ?: "",
                            latitude = document.getDouble("latitude") ?: 0.0,
                            longitude = document.getDouble("longitude") ?: 0.0,
                            name = document.getString("name") ?: "",
                            address = document.getString("address") ?: "",
                            foodtype = document.getString("foodtype") ?: "",
                            review = document.getString("review") ?: "",
                            favourite = document.getBoolean("favourite") ?: false
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
                        val imageUrl = document.getString("imageUrl")

                        val destination = DestinationModel(
                            key = document.id,
                            userEmail = document.getString("userEmail") ?: "",
                            imageUrl = imageUrl ?: "",
                            menu = document.getString("menu") ?: "",
                            latitude = document.getDouble("latitude") ?: 0.0,
                            longitude = document.getDouble("longitude") ?: 0.0,
                            name = document.getString("name") ?: "",
                            address = document.getString("address") ?: "",
                            foodtype = document.getString("foodtype") ?: "",
                            review = document.getString("review") ?: "",
                            favourite = document.getBoolean("favourite") ?: false
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
                        val imageName = document.getString("imageUrl")

                        val destination = DestinationModel(
                            key = document.id,
                            userEmail = document.getString("userEmail") ?: "",
                            imageUrl = imageName ?: "",
                            menu = document.getString("menu") ?: "",
                            latitude = document.getDouble("latitude") ?: 0.0,
                            longitude = document.getDouble("longitude") ?: 0.0,
                            name = document.getString("name") ?: "",
                            address = document.getString("address") ?: "",
                            foodtype = document.getString("foodtype") ?: "",
                            review = document.getString("review") ?: "",
                            favourite = document.getBoolean("favourite") ?: true
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
