package com.example.autodrum

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.autodrum.data.DrumDatabase
import com.example.autodrum.data.DrumRepository
import com.example.autodrum.service.DrumMacroOverlayService
import com.example.autodrum.ui.DrumViewModel
import com.example.autodrum.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = DrumDatabase.getDatabase(this)
        val repository = DrumRepository(database.drumDao())
        val viewModel = DrumViewModel(repository)
        viewModel.loadData()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        MainScreen(viewModel)
                        
                        Button(
                            onClick = { startOverlay() },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(24.dp)
                        ) {
                            Text("START OVERLAY")
                        }
                    }
                }
            }
        }
    }

    private fun startOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        } else {
            startForegroundService(Intent(this, DrumMacroOverlayService::class.java))
        }
    }
}
