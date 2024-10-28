package com.practicum.playlistmaker.share.domain

interface SharingInteractor {
    fun openTerms()
    fun shareApp()
    fun contactSupport()
    fun sharePlaylist(message: String)
}