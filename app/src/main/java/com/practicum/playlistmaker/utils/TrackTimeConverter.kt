package com.practicum.playlistmaker.utils

import java.text.SimpleDateFormat
import java.util.Locale

object TrackTimeConverter {

    fun milsToMinSec(milliseconds: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(milliseconds)
    }

    fun milsToMin(milliseconds: Long): String {
        return SimpleDateFormat("mm", Locale.getDefault()).format(milliseconds)
    }

    fun milsToLessThan10Min(milliseconds: Long): String {
        return SimpleDateFormat("m", Locale.getDefault()).format(milliseconds)
    }

    fun setTrackYear(year: String?): String {
        return year?.substring(0, 4) ?: ""
    }
}