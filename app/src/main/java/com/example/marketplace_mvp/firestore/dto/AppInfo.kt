package com.example.marketplace_mvp.firestore.dto

data class AppInfo(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val version: String = "",
    val iconUrl: String = ""
)