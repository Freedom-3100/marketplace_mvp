package com.example.marketplace_mvp.firestore

import com.example.marketplace_mvp.firestore.dto.AppInfo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AppsRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Получает список всех приложений из коллекции "apps"
     */
    suspend fun getApps(): List<AppInfo> {
        return try {
            val result = db.collection("apps").get().await()
            result.toObjects(AppInfo::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Получает одно приложение по имени (точное совпадение)
     */
    suspend fun getAppByName(name: String): AppInfo? {
        return try {
            val result = db.collection("apps")
                .whereEqualTo("name", name)
                .limit(1)
                .get()
                .await()
            result.toObjects(AppInfo::class.java).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Получает список имён всех приложений (только поле "name")
     */
    suspend fun getAllAppNames(): List<String> {
        return try {
            val result = db.collection("apps").get().await()
            result.documents.mapNotNull { document ->
                document.getString("name")
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}