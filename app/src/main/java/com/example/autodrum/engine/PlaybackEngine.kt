package com.example.autodrum.engine

import android.util.Log
import com.example.autodrum.data.DrumRepository
import com.example.autodrum.data.DrumSheet
import com.example.autodrum.data.KitProfile
import com.example.autodrum.data.KitPiece
import com.example.autodrum.service.DrumMacroAccessibilityService
import kotlinx.coroutines.*

class PlaybackEngine(private val repository: DrumRepository) {
    
    private var playbackJob: Job? = null
    
    suspend fun playSheet(sheet: DrumSheet, latencyOffset: Long) {
        stop()
        
        // Use a default or the first kit from DB. If empty, create a dummy kit mapping.
        val kits = repository.getAllKits()
        val currentKit = if (kits.isNotEmpty()) kits.first() else getDummyKit()

        playbackJob = CoroutineScope(Dispatchers.Default).launch {
            Log.d("PlaybackEngine", "Starting playback for ${sheet.name}")
            
            // Group events by timestamp so we can multi-touch simultaneous hits
            val groupedEvents = sheet.events.groupBy { it.timestampMs }.toSortedMap()
            
            val startTime = System.currentTimeMillis()
            
            for ((timestamp, eventsAtTime) in groupedEvents) {
                // Apply user latency offset (e.g., audio latency tuning)
                val targetTime = startTime + timestamp + latencyOffset
                
                var delayNeeded = targetTime - System.currentTimeMillis()
                
                // Spin-wait or coroutine delay depending on duration
                if (delayNeeded > 10) {
                    delay(delayNeeded - 5) // Delay until 5ms before, then busy-wait for precision
                }
                
                while (System.currentTimeMillis() < targetTime) {
                    if (!isActive) return@launch
                }
                
                if (!isActive) break

                // Find the coordinates for each piece mapped in the kit
                val pointsToTap = eventsAtTime.mapNotNull { event ->
                    val piece = currentKit.pieces.find { it.name.equals(event.pieceName, ignoreCase = true) }
                    if (piece != null) Pair(piece.x, piece.y) else null
                }
                
                if (pointsToTap.isNotEmpty()) {
                    DrumMacroAccessibilityService.instance?.tapMultiple(pointsToTap)
                }
            }
            
            Log.d("PlaybackEngine", "Playback finished")
        }
    }
    
    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        Log.d("PlaybackEngine", "Playback stopped")
    }

    private fun getDummyKit(): KitProfile {
        return KitProfile(
            name = "Dummy Default Kit",
            pieces = listOf(
                KitPiece("Kick", 500f, 1200f),
                KitPiece("Snare", 300f, 1000f),
                KitPiece("Hi-Hat", 200f, 800f),
                KitPiece("Crash", 400f, 600f)
            )
        )
    }
}
