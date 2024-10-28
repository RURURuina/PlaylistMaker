package com.practicum.playlistmaker.mediateka.domain

import android.net.Uri

interface LocalStorageInteractor {
    fun saveImageToLocalStorage(uri: Uri): String
}