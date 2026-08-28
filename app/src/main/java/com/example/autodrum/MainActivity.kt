package com.example.autodrum

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
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
                    Column {
                        MainScreen(viewModel)
                        Button(onClick = { startOverlay() }) {
                            Text("Start Overlay")
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
