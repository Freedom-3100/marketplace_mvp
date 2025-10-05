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
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
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

@Composable
fun InstallButton(
    apkUrl: String,
    buttonText: String = "Скачать",
) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                !context.packageManager.canRequestPackageInstalls()
            ) {
                // Открываем настройки "Неизвестные источники"
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = "package:${context.packageName}".toUri()
                    }
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            } else {
                // Скачиваем APK
                downloadAndInstallApk(context, apkUrl)
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

@SuppressLint("Range")
private fun downloadAndInstallApk(context: Context, url: String) {
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle("Загрузка обновления")
        .setDescription("Пожалуйста, подождите…")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "app_update.apk")
        .setMimeType("application/vnd.android.package-archive")
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)

    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadId = dm.enqueue(request)

    val onComplete = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadId) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = dm.query(query)

                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        val localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                        if (localUri != null) {
                            val apkFile = File(Uri.parse(localUri).path!!)
                            installApk(context, apkFile)
                        } else {
                            Log.e("Installer", "Local URI is null")
                        }
                    } else {
                        Log.e("Installer", "Download failed with status=$status")
                    }
                }
                cursor.close()
                context.unregisterReceiver(this)
            }
        }
    }

    ContextCompat.registerReceiver(
        context,
        onComplete,
        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
        ContextCompat.RECEIVER_NOT_EXPORTED
    )
}

private fun installApk(context: Context, apkFile: File) {
    val apkUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        apkFile
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(apkUri, "application/vnd.android.package-archive")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Log.e("Installer", "No activity found to handle APK install", e)
    }
}
