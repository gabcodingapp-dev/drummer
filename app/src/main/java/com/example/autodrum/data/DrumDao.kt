package com.example.autodrum.data

import androidx.room.*

@Dao
interface DrumDao {
    @Query("SELECT * FROM kit_profiles")
    suspend fun getAllKits(): List<KitProfile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKit(kit: KitProfile)

    @Query("SELECT * FROM drum_sheets")
    suspend fun getAllSheets(): List<DrumSheet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSheet(sheet: DrumSheet)
}
