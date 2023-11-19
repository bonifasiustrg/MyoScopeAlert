package com.apicta.myoscopealert.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.WorkerThread
import com.apicta.myoscopealert.models.Audio
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContentResolverHelper @Inject
constructor(@ApplicationContext val context: Context) {
    private var mCursor: Cursor? = null

    private val projection: Array<String> = arrayOf(
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.DATE_MODIFIED,
    )

//    private var selectionClause: String? =
//        "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ? AND ${MediaStore.Audio.Media.MIME_TYPE} NOT IN (?, ?, ?)"
//    private var selectionArg = arrayOf("1", "audio/amr", "/storage/emulated/0/Music/%.wav", "audio/aac")

//    private var selectionClause: String? =
//        "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ? AND ${MediaStore.Audio.Media.MIME_TYPE} NOT IN (?, ?) AND ${MediaStore.Audio.AudioColumns.DATA} LIKE ?"
//    private var selectionArg = arrayOf("1", "audio/amr", "audio/aac", "/storage/emulated/0/Music/%.wav")

//    private var selectionClause: String? = "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ?"
//    private var selectionArg = arrayOf("1", "audio/amr", "audio/aac", "/storage/emulated/0/Music/%.wav")

    val selection = MediaStore.Audio.Media.DATA + " like ? "

    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    val selectionArgs = arrayOf("%${musicDir.absolutePath}%")
//    val selectionArgs = arrayOf("%/storage/emulated/0/Music/%")

    val sortOrder = MediaStore.Audio.Media.DATE_MODIFIED + " DESC"

//    private val sortOrder = "${MediaStore.Audio.AudioColumns.DATE_MODIFIED} DESC"

    @WorkerThread
    fun getAudioData(): List<Audio> {
        return getCursorData()
    }


    private fun getCursorData(): MutableList<Audio> {
        val audioList = mutableListOf<Audio>()

        mCursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            /*selectionClause*/selection,
            /*selectionArg*/selectionArgs,
            sortOrder
        )

        mCursor?.use { cursor ->
            val idColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val artistColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val dataColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val dateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_MODIFIED)

            cursor.apply {
                if (count == 0) {
                    Log.e("Cursor", "getCursorData: Cursor is Empty")
                } else {
                    while (cursor.moveToNext()) {
                        val displayName = getString(displayNameColumn)
                        val id = getLong(idColumn)
                        val artist = getString(artistColumn)
                        val data = getString(dataColumn)
                        val duration = getInt(durationColumn)
                        val title = getString(titleColumn)
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        val dateModified = getLong(dateModifiedColumn)
                        audioList += Audio(
                            uri, displayName, id, artist, data, duration, title, dateModified
                        )


                    }

                }
            }


        }
        Log.d("ContentResolverHelper", "Number of items in audio list: ${audioList.size}")
        return audioList
    }


}