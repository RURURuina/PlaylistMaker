package com.practicum.playlistmaker.mediateka.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediateka.data.db.TrackInPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackInPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylist(track: TrackInPlaylistEntity)

    @Query("SELECT * FROM track_in_playlist_table WHERE trackId IN (:tracksIdInPlaylist) ORDER BY time DESC")
    fun getTrackInPlaylistById(tracksIdInPlaylist: List<Long>): Flow<List<TrackInPlaylistEntity>>

    @Query("DELETE FROM track_in_playlist_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)
}