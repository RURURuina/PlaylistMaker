package com.practicum.playlistmaker.mediateka.domain

import android.net.Uri

interface LocalStorageRepository {

    fun saveImageToLocalStorage(uri: Uri): String
}