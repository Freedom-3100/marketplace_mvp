package com.example.marketplace_mvp.firestore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketplace_mvp.firestore.dto.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppsViewModel(private val repository: AppsRepository = AppsRepository())
    : ViewModel() {
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null
            try {
                val appsList = repository.getApps()
                _apps.value = appsList
            } catch (e: Exception) {
                _apps.value = emptyList()
                _message.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAppByName(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val foundApp = repository.getAppByName(name)
                if (foundApp != null) {
                    _apps.value = listOf(foundApp)
                    _message.value = null
                } else {
                    _apps.value = emptyList()
                    _message.value = "Приложение '$name' не найдено"
                }
            } catch (e: Exception) {
                _apps.value = emptyList()
                _message.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}