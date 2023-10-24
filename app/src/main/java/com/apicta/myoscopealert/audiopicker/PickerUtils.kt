package com.apicta.myoscopealert.audiopicker


import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File

object PickerUtils {

    val allModes = listOf(
//        PickerMode(PickerType.Image, title = "عکس"),
//        PickerMode(PickerType.Video, "ویدیو"),
//        PickerMode(PickerType.File, "فایل"),
        PickerMode(PickerType.Audio, "موزیک"),
    )

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun permissionState(enableCamera: Boolean): MultiplePermissionsState {
        val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(
//                android.Manifest.permission.READ_MEDIA_IMAGES,
//                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            mutableListOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        }

        if (enableCamera) list.add(android.Manifest.permission.CAMERA)

        return rememberMultiplePermissionsState(list)
    }

    fun getAudio(context: Context): List<PickerFile> {
        val list = mutableListOf<PickerFile>()
        val columns = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.DATA,
        )
        val orderBy = MediaStore.Audio.AudioColumns.DATE_ADDED + " DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            columns,
            null,
            null,
            orderBy
        ) ?: return emptyList()

        while (cursor.moveToNext()) {
            val dataColumnIndex: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            list.add(PickerFile(cursor.getString(dataColumnIndex)))
        }
        cursor.close()
        return list
    }

    fun openFile(context: Context, fileAddress: String?) {
        if (fileAddress == null) return
        try {
            val file = File(fileAddress)
            val map = MimeTypeMap.getSingleton()
            val ext = MimeTypeMap.getFileExtensionFromUrl(file.name)
            val type = map.getMimeTypeFromExtension(ext)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(FileProvider.getUriForFile(context, "${context.packageName}.provider", file), type)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            context.startActivity(intent)
        } catch (e: java.lang.Exception) {
            Toast.makeText(
                context,
                "Can`t open file!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    @Composable
    fun Dp.toPx(): Float {
        val density = LocalDensity.current.density
        return density * value
    }

    @Composable
    fun Int.toDp(): Dp {
        val density = LocalDensity.current.density
        return (this / density).dp
    }

    fun Any?.printToLog(plusTag: String = "", tag: String = "MyLog") {
        Log.d(tag, plusTag + " " + toString())
    }

}