package com.example.marketplace_mvp.ui.components

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.net.toUri
import kotlinx.coroutines.withContext
import java.io.RandomAccessFile

// Помести всё это в нужный файл — примерный self-contained вариант.

@Composable
fun InstallButton(
    apkUrl: String,
    buttonText: String = "Скачать",
) {
    val context = LocalContext.current

    Button(
        onClick = {
            // Для Android O+ проверяем разрешение на установку из "неизвестных источников"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                !context.packageManager.canRequestPackageInstalls()
            ) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = "package:${context.packageName}".toUri()
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    Toast.makeText(context, "Разрешите установку из неизвестных источников и повторите", Toast.LENGTH_LONG).show()
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Не удалось открыть настройки. Дай разрешение вручную.", Toast.LENGTH_LONG).show()
                    Log.e("ApkInstaller", "Open unknown sources settings failed", e)
                }
            } else {
                // Запускаем скачивание + установку
                downloadAndInstallApkDebug(context, apkUrl)
            }
        },
        modifier = Modifier
            .padding(end = 16.dp)
            .height(36.dp)
            .defaultMinSize(minWidth = 80.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = buttonText,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}

private fun downloadAndInstallApkDebug(context: Context, url: String) {
    CoroutineScope(Dispatchers.IO).launch {
        val tag = "ApkInstaller"
        Log.i(tag, "Start download: $url")
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Начинаю загрузку...", Toast.LENGTH_SHORT).show()
        }

        try {
            // 1) Follow redirects manually (max 5)
            var currentUrl = url
            val maxRedirects = 5
            var conn: HttpURLConnection? = null
            var responseCode: Int
            var redirectCount = 0
            while (true) {
                val u = URL(currentUrl)
                conn = (u.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 15000
                    readTimeout = 15000
                    instanceFollowRedirects = false // мы обрабатываем редиректы вручную
                    requestMethod = "GET"
                }
                responseCode = conn.responseCode
                Log.i(tag, "Response code for $currentUrl -> $responseCode")

                if (responseCode in 300..399) {
                    val location = conn.getHeaderField("Location")
                    Log.i(tag, "Redirect to: $location")
                    if (location.isNullOrEmpty()) {
                        throw Exception("Redirect response but no Location header")
                    }
                    currentUrl = URL(u, location).toString() // resolve relative locations
                    conn.disconnect()
                    redirectCount++
                    if (redirectCount > maxRedirects) {
                        throw Exception("Too many redirects")
                    }
                    continue
                }
                break
            }

            if (conn == null) throw Exception("Connection object is null")

            if (responseCode != HttpURLConnection.HTTP_OK) {
                val msg = "Server returned non-OK: $responseCode"
                Log.e(tag, msg)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка загрузки: $responseCode", Toast.LENGTH_LONG).show()
                }
                conn.disconnect()
                return@launch
            }

            // 2) Сохраняем в cacheDir
            val file = File(context.cacheDir, "app_update.apk")
            conn.inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            conn.disconnect()

            Log.i(tag, "Saved to ${file.absolutePath} (size=${file.length()})")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Загрузка завершена: ${file.length()} байт", Toast.LENGTH_SHORT).show()
            }

            // 3) Быстрая проверка "магических байт" ZIP (APK — zip-based)
            val header = ByteArray(4)
            RandomAccessFile(file, "r").use { raf ->
                raf.seek(0)
                raf.readFully(header)
            }
            val isZip = header.size >= 2 && header[0] == 0x50.toByte() && header[1] == 0x4B.toByte()
            if (!isZip) {
                val hex = header.joinToString(" ") { String.format("%02X", it) }
                Log.e(tag, "File is not ZIP/APK magic bytes: $hex")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Файл не является APK (header: $hex)", Toast.LENGTH_LONG).show()
                }
                return@launch
            }

            // 4) Прежде чем запускать установщик — проверим разрешение O+ (на случай, если юзер ещё не подтвердил)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !context.packageManager.canRequestPackageInstalls()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Нет разрешения на установку из неизвестных источников. Откройте настройки.", Toast.LENGTH_LONG).show()
                }
                return@launch
            }

            // 5) Выдаём URI через FileProvider и даём разрешение всем activity-обработчикам
            withContext(Dispatchers.Main) {
                try {
                    val apkUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        file
                    )
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(apkUri, "application/vnd.android.package-archive")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    // Grant URI permission to all activities that can handle the intent
                    val resList = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                    for (res in resList) {
                        val pkgName = res.activityInfo.packageName
                        context.grantUriPermission(pkgName, apkUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        Log.i(tag, "Grant uri permission to $pkgName")
                    }

                    // Start installer
                    Log.i(tag, "Starting installer intent. resolvers=${resList.size}")
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e(tag, "Failed to start installer", e)
                    Toast.makeText(context, "Не удалось запустить установщик: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

        } catch (e: Exception) {
            Log.e("ApkInstaller", "Download/Install error", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
