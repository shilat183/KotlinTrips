package com.trips.project.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.trips.project.data.model.DestinationModel

@Dao
interface DestinationDao {
    @Query("SELECT * FROM Destination")
    fun getAll(): LiveData<List<DestinationModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(destinations: List<DestinationModel>)



    @Query("DELETE FROM Destination")
    fun deleteAll()

    @Delete
    fun delete(destination: DestinationModel)

    @Update
    fun update(destination: DestinationModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(destination: DestinationModel)
}
