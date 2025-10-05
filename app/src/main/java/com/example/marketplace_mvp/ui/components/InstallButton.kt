package com.example.marketplace_mvp.ui.components

import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                !context.packageManager.canRequestPackageInstalls()
            ) {
                // Open settings for unknown app installs
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = "package:${context.packageName}".toUri()
                    }
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            } else {
                // Download and install APK
                scope.launch {
                    downloadAndInstallApk(context, apkUrl)
                }
            }
        },
        modifier = Modifier
            .padding(end = 16.dp)
            .height(36.dp) // 👈 smaller height
            .defaultMinSize(minWidth = 80.dp), // 👈 optional width control
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), // 👈 tighter padding
    ) {
        Text(
            text = buttonText,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}

private fun downloadAndInstallApk(context: Context, url: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connect()
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "app_update.apk"
            )
            val output = FileOutputStream(file)
            connection.inputStream.use { input ->
                output.use { out -> input.copyTo(out) }
            }

            // Trigger install on main thread
            CoroutineScope(Dispatchers.Main).launch {
                installApk(context, file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun installApk(context: Context, apkFile: File) {
    val packageInstaller = context.packageManager.packageInstaller
    val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
    val sessionId = packageInstaller.createSession(params)
    val session = packageInstaller.openSession(sessionId)

    // Copy the APK into the session
    apkFile.inputStream().use { input ->
        session.openWrite("app_install", 0, -1).use { output ->
            input.copyTo(output)
            session.fsync(output)
        }
    }

    // Create an Intent to get installation result
    val intent = Intent(context, InstallResultReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    session.commit(pendingIntent.intentSender)
    session.close()
}

class InstallResultReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
        val message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
        if (status == PackageInstaller.STATUS_SUCCESS) {
            Log.d("Installer", "Installation succeeded!")
        } else {
            Log.e("Installer", "Installation failed: $message")
        }
    }
}