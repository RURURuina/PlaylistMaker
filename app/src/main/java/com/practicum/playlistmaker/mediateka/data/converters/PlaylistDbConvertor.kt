package com.practicum.playlistmaker.mediateka.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.mediateka.data.db.PlaylistEntity
import com.practicum.playlistmaker.mediateka.domain.model.Playlist

object PlaylistDbConvertor {

    fun Playlist.toPlaylistEntity(): PlaylistEntity {
        return PlaylistEntity(
            playlistId,
            playlistName,
            playlistDescription,
            uri,
            fromListLongToString(tracksIdInPlaylist),
            tracksCount
        )
    }

    fun PlaylistEntity.toPlaylist(): Playlist {
        return Playlist(
            playlistId,
            playlistName,
            playlistDescription,
            uri,
            fromStingToListLong(tracksIdInPlaylist),
            tracksCount
        )

    }
    @TypeConverter
    private fun fromListLongToString(value: List<Long>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    private fun fromStingToListLong(value:String): List<Long> {
        val listType = object: TypeToken<List<Long>>() {}.type
        return Gson().fromJson(value, listType)
    }
}