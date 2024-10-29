package com.practicum.playlistmaker.mediateka.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.practicum.playlistmaker.mediateka.domain.LocalStorageRepository
import java.io.File
import java.io.FileOutputStream

class LocalStorageRepositoryImpl(private val context: Context) : LocalStorageRepository {

    companion object {
        const val FILE_NAME = "cover_playlist.jpg"
        const val DIRECTORY_NAME = "playlist"
    }

    override fun saveImageToLocalStorage(uri: Uri): String {
        val fileName = generateFileName()
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), DIRECTORY_NAME)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, fileName)
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.toURI().toString()
    }

    private fun generateFileName(): String {
        return "playlist_cover_${System.currentTimeMillis()}.jpg"
    }
}