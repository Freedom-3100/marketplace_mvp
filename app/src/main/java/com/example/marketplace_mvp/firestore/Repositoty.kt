package com.example.marketplace_mvp.firestore

import com.example.marketplace_mvp.firestore.dto.AppInfo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// AppsRepository.kt - Add these methods to your existing repository
class AppsRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // ... your existing methods ...

    /**
     * Search apps by name (case-insensitive partial match)
     */
    suspend fun searchApps(query: String): List<AppInfo> {
        return try {
            val result = db.collection("apps").get().await()
            val allApps = result.toObjects(AppInfo::class.java)

            // Client-side filtering for partial match
            allApps.filter { app ->
                app.name.contains(query, ignoreCase = true) ||
                        app.description.contains(query, ignoreCase = true) ||
                        app.category.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Search apps by category
     */
    suspend fun getAppsByCategory(category: String): List<AppInfo> {
        return try {
            val result = db.collection("apps")
                .whereEqualTo("category", category)
                .get()
                .await()
            result.toObjects(AppInfo::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

