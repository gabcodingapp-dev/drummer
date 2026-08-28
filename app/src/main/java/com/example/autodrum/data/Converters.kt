package com.example.autodrum.data

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val piecesType = Types.newParameterizedType(List::class.java, KitPiece::class.java)
    private val eventsType = Types.newParameterizedType(List::class.java, DrumEvent::class.java)

    @TypeConverter
    fun fromKitPieces(pieces: List<KitPiece>?): String {
        return moshi.adapter<List<KitPiece>>(piecesType).toJson(pieces ?: emptyList())
    }

    @TypeConverter
    fun toKitPieces(json: String?): List<KitPiece> {
        return if (json.isNullOrEmpty()) emptyList() else moshi.adapter<List<KitPiece>>(piecesType).fromJson(json) ?: emptyList()
    }

    @TypeConverter
    fun fromDrumEvents(events: List<DrumEvent>?): String {
        return moshi.adapter<List<DrumEvent>>(eventsType).toJson(events ?: emptyList())
    }

    @TypeConverter
    fun toDrumEvents(json: String?): List<DrumEvent> {
        return if (json.isNullOrEmpty()) emptyList() else moshi.adapter<List<DrumEvent>>(eventsType).fromJson(json) ?: emptyList()
    }
}
