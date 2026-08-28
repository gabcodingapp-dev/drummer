package com.example.autodrum.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(viewModel: DrumViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("AutoDrum Main Screen")
        Text("Kits and Sheets will be listed here.")
    }
}
