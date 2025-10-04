package com.example.marketplace_mvp.firestore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketplace_mvp.firestore.dto.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppsViewModel(private val repository: AppsRepository = AppsRepository()) : ViewModel() {
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val appsList = repository.getApps()
                _apps.value = appsList
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
}