package com.practicum.playlistmaker.mediateka.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.mediateka.data.db.dao.FavoriteTrackDao

@Database(entities = [FavoriteTrackEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao
}