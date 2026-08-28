package com.example.autodrum.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [KitProfile::class, DrumSheet::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DrumDatabase : RoomDatabase() {
    abstract fun drumDao(): DrumDao

    companion object {
        @Volatile
        private var INSTANCE: DrumDatabase? = null

        fun getDatabase(context: Context): DrumDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrumDatabase::class.java,
                    "drum_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
