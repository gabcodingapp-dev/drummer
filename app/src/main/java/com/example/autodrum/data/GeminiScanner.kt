package com.example.autodrum.data

class GeminiScanner {
    fun scanImage(imageBytes: ByteArray): DrumSheet {
        // Dummy implementation
        return DrumSheet(name = "AI Scanned Sheet", bpm = 120, events = emptyList())
    }
}
