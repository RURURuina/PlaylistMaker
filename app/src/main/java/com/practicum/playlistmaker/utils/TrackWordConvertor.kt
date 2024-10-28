package com.practicum.playlistmaker.utils

class TrackWordConvertor() {
    companion object {

        fun getTrackWord(count: Int): String {
            val trackWord = when {
                count % 10 == 1 && count % 100 != 11 -> "трек"
                count % 10 in 2..4 && (count % 100 !in 12..14) -> "трека"
                else -> "треков"
            }
            return trackWord
        }

        fun getMinutesWord(count: Int): String {
            val lastDigit = count % 10
            val lastTwoDigits = count % 100

            val minutesWord = when {
                lastTwoDigits in 11..14 -> " минут"
                lastDigit == 1 -> " минута"
                lastDigit in 2..4 -> " минуты"
                else -> " минут"
            }
            return minutesWord
        }
    }

}