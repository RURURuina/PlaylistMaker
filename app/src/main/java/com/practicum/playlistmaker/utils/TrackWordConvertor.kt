package com.practicum.playlistmaker.utils

import android.view.View
import com.practicum.playlistmaker.R

class TrackWordConvertor() {
    companion object {
        fun getTrackCountString(count: Int, view: View): String {
            val remainder10 = count % 10
            val remainder100 = count % 100

            return when {
                remainder100 in 11..19 -> view.context.getString(R.string.track_count_5)
                remainder10 == 1 -> view.context.getString(R.string.track_count_1)
                remainder10 in 2..4 -> view.context.getString(R.string.track_count_2_4)
                else -> view.context.getString(R.string.track_count_5)
        }
    }}

}