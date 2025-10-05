package com.example.marketplace_mvp.firestore.dto

data class AppInfo(
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val creator: String = "",
    val downloads: String = "",
    val size: String = "",
    val ageRating: String = "",
    val mark: String = "0",
    val iconUrl: String = "",
    val apkUrl: String = ""
) {

    val imageUrl: String
        get() = iconUrl.trim()

    val cleanApkUrl: String
        get() = apkUrl.trim()
}