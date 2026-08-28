package com.example.autodrum.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: DrumViewModel) {
    val kits by viewModel.kits.collectAsState()
    val sheets by viewModel.sheets.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("AutoDrum") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text("Your Drum Kits", style = MaterialTheme.typography.titleMedium)
            if (kits.isEmpty()) {
                Text("No kits calibrated. Calibrate via overlay.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(modifier = Modifier.height(150.dp)) {
                    items(kits) { kit ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text(kit.name, modifier = Modifier.padding(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Drum Sheets", style = MaterialTheme.typography.titleMedium)
            if (sheets.isEmpty()) {
                Text("No sheets imported yet.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(sheets) { sheet ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text("${sheet.name} (${sheet.bpm} BPM)", modifier = Modifier.padding(12.dp))
                        }
                    }
                }
            }
        }
    }
}
