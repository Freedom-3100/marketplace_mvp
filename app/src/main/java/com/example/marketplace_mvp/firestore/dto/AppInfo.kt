package com.example.marketplace_mvp.firestore.dto

data class AppInfo(
    val id: String = "",
    val name: String = "",
    val mark: String = "",
    val ageRating: String = "",
    val creator: String = "",
    val category: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val apkUrl: String = ""
)