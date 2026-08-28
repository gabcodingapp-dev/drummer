package com.example.autodrum.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KitPiece(
    val name: String,
    val x: Float,
    val y: Float
)

@Entity(tableName = "kit_profiles")
@JsonClass(generateAdapter = true)
data class KitProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val pieces: List<KitPiece> // Stored as JSON string via TypeConverter
)

@JsonClass(generateAdapter = true)
data class DrumEvent(
    val pieceName: String,
    val timestampMs: Long
)

@Entity(tableName = "drum_sheets")
@JsonClass(generateAdapter = true)
data class DrumSheet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val bpm: Int,
    val events: List<DrumEvent> // Stored as JSON string via TypeConverter
)
