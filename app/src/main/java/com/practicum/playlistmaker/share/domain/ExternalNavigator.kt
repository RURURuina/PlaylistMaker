package com.practicum.playlistmaker.share.domain

interface ExternalNavigator {
    fun share()
    fun openLink()
    fun sendEmail()
    fun sharePlaylist(message: String)
}