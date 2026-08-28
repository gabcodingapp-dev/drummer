package com.example.autodrum.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class DrumMacroAccessibilityService : AccessibilityService() {

    companion object {
        var instance: DrumMacroAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("DrumMacro", "Accessibility Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    /**
     * Dispatches simultaneous taps for a "chord" (multiple drums hit at once).
     * Android supports up to 10 simultaneous strokes in a GestureDescription.
     */
    fun tapMultiple(points: List<Pair<Float, Float>>) {
        if (points.isEmpty()) return

        val builder = GestureDescription.Builder()
        // Android typically limits to 10 max simultaneous strokes
        val limitedPoints = points.take(10)

        for ((x, y) in limitedPoints) {
            val path = Path().apply { moveTo(x, y) }
            // 10ms stroke duration to emulate a very quick tap
            val stroke = GestureDescription.StrokeDescription(path, 0, 10)
            builder.addStroke(stroke)
        }

        val gesture = builder.build()
        val success = dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                // Log.d("DrumMacro", "Dispatched ${points.size} simultaneous taps")
            }
            override fun onCancelled(gestureDescription: GestureDescription?) {
                Log.e("DrumMacro", "Gesture cancelled!")
            }
        }, null)

        if (!success) {
            Log.e("DrumMacro", "dispatchGesture failed to enqueue!")
        }
    }
}
