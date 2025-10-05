package com.example.marketplace_mvp.firestore

import com.example.marketplace_mvp.firestore.dto.AppInfo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AppsRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getApps(): List<AppInfo> {
        val result = db.collection("apps").get().await()
        return result.toObjects(AppInfo::class.java)
    }

    suspend fun getAppByName(name: String): AppInfo? {
        val result = db.collection("apps")
            .whereEqualTo("name", name)
            .limit(1)
            .get()
            .await()
        return result.toObjects(AppInfo::class.java).firstOrNull()
    }
}