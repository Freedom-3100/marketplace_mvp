package com.example.marketplace_mvp.firestore.dto

data class AppInfo(
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val creator: String = "",
    val downloads: String = "",
    val size: String = "",
    val ageRating: String = "",
    val mark: String = "0",        // ← остаётся строкой: "4,8"
    val iconUrl: String = "",      // ← может содержать пробелы
    val apkUrl: String = ""        // ← может содержать пробелы
) {
    // Геттеры для очистки URL (безопасные)
    val imageUrl: String
        get() = iconUrl.trim()

    val cleanApkUrl: String
        get() = apkUrl.trim()
}