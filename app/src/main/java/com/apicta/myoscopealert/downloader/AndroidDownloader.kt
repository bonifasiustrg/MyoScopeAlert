package com.apicta.myoscopealert.downloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File

class AndroidDownloader(
    private val context: Context
) : Downloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(url: String): Long {
        val uri = Uri.parse(url)
        val fileName = uri.lastPathSegment ?: "unknown.wav"
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), fileName)

        if (file.exists()) {
            Log.e("file download", "File $fileName already exists. Skipping download.")
            return -1L
        }

        val request = DownloadManager.Request(uri)
            .setMimeType("audio/wav")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE) // Izinkan Wi-Fi & seluler
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(fileName)
            .addRequestHeader("Authorization", "Bearer 112|IP8WIhZ24YizhOQU7h4xzibLbfr2mecKKzafLv58618a843d")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, fileName)

        return downloadManager.enqueue(request)
    }
}

