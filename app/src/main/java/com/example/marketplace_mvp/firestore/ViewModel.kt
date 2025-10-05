package com.example.marketplace_mvp.firestore

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketplace_mvp.firestore.dto.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppsViewModel(
    private val repository: AppsRepository = AppsRepository()
) : ViewModel() {

    // === Список всех приложений ===
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps

    // === Список имён всех приложений ===
    private val _appNames = MutableStateFlow<List<String>>(emptyList())
    val appNames: StateFlow<List<String>> = _appNames

    // === Состояние загрузки (общее) ===
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // === Сообщение об ошибке или уведомление ===
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _cachedApps = mutableStateMapOf<String, AppInfo?>()
    val cachedApps: Map<String, AppInfo?> = _cachedApps

    /**
     * Ленивая загрузка AppInfo по имени (с кешированием)
     */
    fun loadAppInfoByName(name: String) {
        // Если уже загружали — ничего не делаем
        if (_cachedApps.containsKey(name)) return

        // Помечаем, что начали загрузку (можно null или заглушку)
        _cachedApps[name] = null

        viewModelScope.launch {
            try {
                val app = repository.getAppByName(name)
                _cachedApps[name] = app
            } catch (e: Exception) {
                _message.value = "Ошибка загрузки '$name': ${e.message}"
                _cachedApps[name] = null // или оставить как есть
            }
        }
    }
    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null
            try {
                _apps.value = repository.getApps()
            } catch (e: Exception) {
                _apps.value = emptyList()
                _message.value = "Ошибка загрузки приложений: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Загружает одно приложение по имени
     */
    fun loadAppByName(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null
            try {
                val app = repository.getAppByName(name)
                _apps.value = if (app != null) listOf(app) else emptyList()
                if (app == null) {
                    _message.value = "Приложение '$name' не найдено"
                }
            } catch (e: Exception) {
                _apps.value = emptyList()
                _message.value = "Ошибка поиска: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Загружает список имён всех приложений
     */
    fun loadAllAppNames() {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null
            try {
                _appNames.value = repository.getAllAppNames()
            } catch (e: Exception) {
                _appNames.value = emptyList()
                _message.value = "Ошибка загрузки списка имён: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --------------------------------------------------
    // Опционально: если захочешь очищать состояние
    fun clearApps() {
        _apps.value = emptyList()
        _appNames.value = emptyList()
        _message.value = null
    }
}