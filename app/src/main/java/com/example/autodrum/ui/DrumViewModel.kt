package com.example.autodrum.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autodrum.data.DrumRepository
import com.example.autodrum.data.DrumSheet
import com.example.autodrum.data.KitProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DrumViewModel(private val repository: DrumRepository) : ViewModel() {
    private val _kits = MutableStateFlow<List<KitProfile>>(emptyList())
    val kits: StateFlow<List<KitProfile>> = _kits

    private val _sheets = MutableStateFlow<List<DrumSheet>>(emptyList())
    val sheets: StateFlow<List<DrumSheet>> = _sheets

    fun loadData() {
        viewModelScope.launch {
            _kits.value = repository.getAllKits()
            _sheets.value = repository.getAllSheets()
        }
    }
}
