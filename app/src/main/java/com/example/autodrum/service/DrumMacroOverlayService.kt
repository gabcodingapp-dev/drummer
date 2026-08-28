package com.example.autodrum.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.autodrum.engine.PlaybackEngine
import com.example.autodrum.data.DrumDatabase
import com.example.autodrum.data.DrumRepository
import com.example.autodrum.data.DrumSheet
import com.example.autodrum.data.DrumEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DrumMacroOverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var lifecycleOwner: OverlayLifecycleOwner
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var playbackEngine: PlaybackEngine

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val channel = NotificationChannel("drum_overlay", "Overlay Service", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        
        val notification = Notification.Builder(this, "drum_overlay")
            .setContentTitle("DrumMacro Overlay")
            .setContentText("Controls are floating on screen.")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()
        startForeground(1, notification)

        // Initialize Repository & Engine
        val db = DrumDatabase.getDatabase(this)
        val repo = DrumRepository(db.drumDao())
        playbackEngine = PlaybackEngine(repo)
        
        setupComposeView()
    }

    private fun setupComposeView() {
        lifecycleOwner = OverlayLifecycleOwner()
        lifecycleOwner.onCreate()

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent {
                OverlayContent()
            }
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 200
        }

        windowManager.addView(composeView, params)
        lifecycleOwner.onStart()
        lifecycleOwner.onResume()
    }

    @Composable
    fun OverlayContent() {
        var offsetX by remember { mutableFloatStateOf(params.x.toFloat()) }
        var offsetY by remember { mutableFloatStateOf(params.y.toFloat()) }
        var isPlaying by remember { mutableStateOf(false) }
        var latencyMs by remember { mutableFloatStateOf(0f) }

        // Draggable container
        Card(
            modifier = Modifier
                .width(260.dp)
                .padding(8.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        params.x = offsetX.toInt()
                        params.y = offsetY.toInt()
                        windowManager.updateViewLayout(composeView, params)
                    }
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("AutoDrum Macro", color = Color.White, style = MaterialTheme.typography.titleMedium)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Playback Controls
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { 
                            if(isPlaying) { playbackEngine.stop(); isPlaying = false }
                            else { startDummyPlayback(latencyMs.toLong()); isPlaying = true }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isPlaying) Color.Red else Color(0xFF4CAF50))
                    ) {
                        Text(if (isPlaying) "Stop" else "Play")
                    }
                    Button(
                        onClick = { testPads() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("Test")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                // Latency Slider
                Text("Latency Offset: ${latencyMs.toInt()} ms", color = Color.LightGray, style = MaterialTheme.typography.labelSmall)
                Slider(
                    value = latencyMs,
                    onValueChange = { latencyMs = it },
                    valueRange = -500f..500f,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Drag panel to move", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                
                Button(
                    onClick = { stopSelf() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text("Close")
                }
            }
        }
    }

    private fun testPads() {
        val tapService = DrumMacroAccessibilityService.instance
        if (tapService != null) {
            // Tap a couple of points on the screen instantly for testing
            tapService.tapMultiple(listOf(Pair(500f, 1000f), Pair(600f, 1000f)))
        }
    }

    private fun startDummyPlayback(latencyOffset: Long) {
        serviceScope.launch {
            // Create a dummy sheet for testing the engine
            val events = listOf(
                DrumEvent("Kick", 500),
                DrumEvent("Hi-Hat", 500), // Simultaneous
                DrumEvent("Snare", 1000),
                DrumEvent("Hi-Hat", 1000),
                DrumEvent("Kick", 1500),
                DrumEvent("Crash", 2000)
            )
            val sheet = DrumSheet(name = "Test Groove", bpm = 120, events = events)
            
            playbackEngine.playSheet(sheet, latencyOffset)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playbackEngine.stop()
        if (::lifecycleOwner.isInitialized) {
            lifecycleOwner.onPause()
            lifecycleOwner.onStop()
            lifecycleOwner.onDestroy()
        }
        if (::composeView.isInitialized) {
            windowManager.removeView(composeView)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
