package com.example.autodrum.data

object DrumLabels {
    fun matchLabel(input: String): String {
        val lower = input.lowercase()
        if (lower.contains("hi-hat") || lower.contains("hh")) return "Hi-Hat"
        if (lower.contains("snare")) return "Snare"
        if (lower.contains("kick")) return "Kick"
        if (lower.contains("tom")) return "Tom"
        if (lower.contains("crash")) return "Crash"
        if (lower.contains("ride")) return "Ride"
        if (lower.contains("china")) return "China"
        if (lower.contains("splash")) return "Splash"
        return "Unknown"
    }
}
