package com.example.autodrum.data

class MidiParser {
    fun parse(midiData: ByteArray): DrumSheet {
        // Dummy implementation
        return DrumSheet(name = "Imported MIDI", bpm = 120, events = emptyList())
    }
}
