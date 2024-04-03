package com.trips.project.data.model
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trips.project.data.db.DestinationDao

class DestinationModelFactory(private val destinationDao: DestinationDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DestinationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DestinationViewModel(destinationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
