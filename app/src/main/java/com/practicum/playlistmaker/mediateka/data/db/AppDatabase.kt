package com.practicum.playlistmaker.mediateka.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.mediateka.data.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.mediateka.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.mediateka.data.db.dao.TrackInPlaylistDao

@Database(
    entities = [FavoriteTrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackInPlaylistDao(): TrackInPlaylistDao
}