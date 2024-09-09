package com.practicum.playlistmaker.mediateka.ui

import TrackAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.mediateka.ui.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.mediateka.ui.viewmodel.models.FavoriteTracksState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentFavorites : Fragment() {

    companion object {

        const val CLICK_DEBOUNCE_DELAY = 300L
        const val AUDIO_PLAYER_KEY = "track"
        const val IS_FAVORITE = "favorite"
        fun newInstance() = FragmentFavorites()
    }

    private val favoritesViewModel: FavoritesViewModel by viewModel<FavoritesViewModel>()


    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private var adapter: TrackAdapter? = null

    private lateinit var clickDebounce: (Track) -> Unit

    private var isClickAllowed = true

    private lateinit var mediatekaText: TextView
    private lateinit var nothingFoundMediateka: ImageView
    private lateinit var favoriteTracksList: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediatekaText = binding.mediatekaText
        nothingFoundMediateka = binding.nothingFoundMediateka
        favoriteTracksList = binding.favoriteTracksList

        clickDebounce = debounce(
            delayMillis = CLICK_DEBOUNCE_DELAY,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = true
        ) { track ->
            isClickAllowed = true
            openAudioPlayer(track)
        }

        adapter = TrackAdapter(object : TrackAdapter.OnItemClickListener {
            override fun onItemClick(track: Track) {
                if (isClickAllowed) {
                    isClickAllowed = false
                    clickDebounce(track)
                }
            }
        })

        favoriteTracksList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        favoriteTracksList.adapter = adapter

        favoritesViewModel.getFavoriteTracks()

        favoritesViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
        favoriteTracksList.adapter = null
    }

    private fun render(state: FavoriteTracksState) {
        when (state) {
            is FavoriteTracksState.Content -> {
                showContent(state.favoriteTracks)
            }
            is FavoriteTracksState.Empty -> {
                Log.d("Mediateka", "State is Empty")
                showEmpty()}
        }
    }

    private fun showEmpty() {
        favoriteTracksList.visibility = View.GONE
        nothingFoundMediateka.visibility = View.VISIBLE
        mediatekaText.visibility = View.VISIBLE
    }

    private fun showContent(favoriteTracks: List<Track>) {
        Log.d("Mediateka", "Showing content with ${favoriteTracks.size} tracks")
        favoriteTracksList.visibility = View.VISIBLE
        nothingFoundMediateka.visibility = View.GONE
        mediatekaText.visibility = View.GONE

        adapter?.trackList?.clear()

        adapter?.trackList?.addAll(favoriteTracks)

        adapter?.notifyDataSetChanged()
    }

    private fun openAudioPlayer(track: Track) {
        val action = MediatekaFragmentDirections.actionMediatekaFragmentToAudioPlayerFragment(track)
        findNavController().navigate(action)
    }
}