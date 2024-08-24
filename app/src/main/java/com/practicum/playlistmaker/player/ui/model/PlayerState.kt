package com.practicum.playlistmaker.player.ui.model

sealed interface PlayerState {
    object DEFAULT : PlayerState
    object PREPARED : PlayerState
    object PLAYING : PlayerState
    object PAUSED : PlayerState
    data class CurrentPosition(val time: String) : PlayerState
}