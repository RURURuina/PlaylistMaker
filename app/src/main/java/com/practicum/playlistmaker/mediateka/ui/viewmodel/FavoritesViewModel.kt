package  com.practicum.playlistmaker.mediateka.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediateka.domain.db.FavoriteTrackInteractor
import com.practicum.playlistmaker.mediateka.ui.viewmodel.models.FavoriteTracksState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {

    init {
        getFavoriteTracks()
    }

    private val stateLiveData = MutableLiveData<FavoriteTracksState>()
    fun observeState(): LiveData<FavoriteTracksState> = stateLiveData


    fun getFavoriteTracks() {
        viewModelScope.launch {
            favoriteTrackInteractor.getFavoriteTracks().collect() {favoriteTracks ->
                processResult(favoriteTracks)
            }
        }
    }
    private fun processResult(favoriteTracks: List<Track>) {
        if (favoriteTracks.isEmpty()) {
            renderState(FavoriteTracksState.Empty)
        } else {
            renderState(FavoriteTracksState.Content(favoriteTracks))
        }
    }

    private fun renderState(state: FavoriteTracksState) {
        stateLiveData.postValue(state)
    }
}