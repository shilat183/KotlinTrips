package com.trips.project.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.trips.project.data.model.DestinationModel

@Database(entities = [DestinationModel::class], version = 4)
abstract class DestinationDatabse : RoomDatabase() {

    abstract fun destinationDao(): DestinationDao

    companion object {
        @Volatile
        private var INSTANCE: DestinationDatabse? = null

        // Migration from version 3 to 4
        val migration3to4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Destination ADD COLUMN new_column_name TEXT DEFAULT ''")
            }
        }

        fun getInstance(context: Context): DestinationDatabse {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DestinationDatabse::class.java,
                    "destination_database" // Change database name to "destination_database"
                ).addMigrations(migration3to4) // Add migration from version 3 to 4
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
