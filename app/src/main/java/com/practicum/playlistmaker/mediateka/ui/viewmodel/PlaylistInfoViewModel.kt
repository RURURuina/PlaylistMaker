package com.practicum.playlistmaker.mediateka.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import com.practicum.playlistmaker.mediateka.ui.viewmodel.models.TracksInPlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.share.domain.SharingInteractor
import com.practicum.playlistmaker.utils.TrackTimeConverter
import com.practicum.playlistmaker.utils.TrackWordConvertor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val playlistsInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private var playlist: Playlist? = null
    private var tracksList = arrayListOf<Track>() as List<Track>

    companion object {
        private const val tenMin: Long = 600000
    }

    private val playlistLiveData = MutableLiveData<Playlist>()
    private val tracksInPlaylistLiveData = MutableLiveData<List<Track>>()
    private val tracksDurationLiveData = MutableLiveData<String>()
    private val renderTracksLiveData = MutableLiveData<TracksInPlaylistState>()

    fun observeStatePlaylist(): LiveData<Playlist> = playlistLiveData
    fun observeStateTracksInPlaylist(): LiveData<List<Track>> = tracksInPlaylistLiveData
    fun observeStateTracksDuration(): LiveData<String> = tracksDurationLiveData
    fun observeStateRenderTracksLiveData(): LiveData<TracksInPlaylistState> = renderTracksLiveData

    fun getPlaylistById(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getPlaylistById(playlistId).collect { playlistById ->
                if (playlistById != null) {
                    playlist = playlistById
                    playlistLiveData.postValue(playlist!!)
                    getTracksInPlaylistById(playlist!!.tracksIdInPlaylist)
                }
            }
        }
    }

    private fun getTracksInPlaylistById(tracksIdInPlaylist: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getTracksInPlaylistById(tracksIdInPlaylist)
                .collect { tracksInPlaylist ->
                    tracksList = tracksInPlaylist
                    tracksInPlaylistLiveData.postValue(tracksList)
                    getTracksDuration(tracksList)
                    renderTracksInPlaylist(tracksList)
                }
        }
    }

    private fun getTracksDuration(tracksInPlaylist: List<Track>) {
        var tracksDuration: Long = 0
        tracksInPlaylist?.forEach { track ->
            tracksDuration += track.trackTimeMillis
        }
        val timeInMin: String
        if (tracksDuration < tenMin) {
            timeInMin = TrackTimeConverter.milsToLessThan10Min(tracksDuration)
        } else {
            timeInMin = TrackTimeConverter.milsToMin(tracksDuration)
        }
        tracksDurationLiveData.postValue(timeInMin + TrackWordConvertor.getMinutesWord(timeInMin.toInt()))
    }

    private fun renderTracksInPlaylist(tracksInPlaylist: List<Track>) {
        if (tracksInPlaylist.isEmpty()) {
            renderTracksInPlaylistState(TracksInPlaylistState.Empty)
        } else {
            renderTracksInPlaylistState(TracksInPlaylistState.Content(tracksInPlaylist))
        }
    }

    private fun renderTracksInPlaylistState(state: TracksInPlaylistState) {
        renderTracksLiveData.postValue(state)
    }

    fun deleteTracksInPlaylist(trackId: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.deleteTrackInPlaylist(playlist!!, trackId!!)
        }
    }

    fun sharePlaylist() {
        var trackInfo: String = ""
        var i = 1
        tracksList.forEach { track ->
            trackInfo += "$i." + "${track.artistName} - " + "${track.trackName} " + "(${
                TrackTimeConverter.milsToMinSec(
                    track.trackTimeMillis
                )
            })\n"
            i++
        }
        if (playlist != null) {
            val message: String =
                "Плейлист \"${playlist!!.playlistName}\"\n" + "${playlist!!.playlistDescription ?: ""}\n" + "${playlist!!.tracksCount} ${
                    TrackWordConvertor.getTrackWord(playlist!!.tracksCount)
                }:\n" + "${trackInfo}"
            sharingInteractor.sharePlaylist(message)
        }
    }

    fun deletePlaylist() {
        if (playlist != null) {
            viewModelScope.launch(Dispatchers.IO) {
                playlistsInteractor.deletePlaylist(playlist!!)
            }
        }
    }
}