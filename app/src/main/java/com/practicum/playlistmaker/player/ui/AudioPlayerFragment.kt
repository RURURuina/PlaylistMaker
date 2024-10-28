package com.practicum.playlistmaker.player.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioplayerBinding
import com.practicum.playlistmaker.player.ui.adapters.BottomSheetPlaylistAdapter
import com.practicum.playlistmaker.player.ui.model.PlayerState
import com.practicum.playlistmaker.player.ui.model.TrackInPlaylistState
import com.practicum.playlistmaker.player.ui.view_model.MediaPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.adapters.TrackViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerFragment : Fragment() {

    private val viewModel by viewModel<MediaPlayerViewModel>()

    private var _binding: FragmentAudioplayerBinding? = null
    private val binding get() = _binding!!

    private val args: AudioPlayerFragmentArgs by navArgs()

    private lateinit var track: Track

    private var adapter: BottomSheetPlaylistAdapter? = null
    private lateinit var bottomSheet: BottomSheetBehavior<LinearLayout>

    companion object {
        private const val TRACK_ARTWORK_SIZE = "512x512bb.jpg"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track = args.track

        binding.backButton.setOnClickListener { findNavController().navigateUp() }

        binding.addToPlaylist.setOnClickListener {
            viewModel.getSavedPlaylists()
            showBottomSheet()
        }

        adapter = BottomSheetPlaylistAdapter()
        binding.recyclerViewPlayer.adapter = adapter
        binding.recyclerViewPlayer.layoutManager = LinearLayoutManager(requireContext())

        bottomSheet = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.playerOverlay.isVisible = false
                    }

                    else -> {
                        binding.playerOverlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.playerOverlay.alpha = slideOffset
            }
        })

        binding.playIc.setOnClickListener { viewModel.playbackControl() }
        binding.favorite.setOnClickListener { viewModel.onFavoriteClicked(track) }

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName


        if (track.artworkUrl100?.isNotEmpty() == true) {
            Glide.with(this).load(track.artworkUrl100!!.replaceAfterLast("/", TRACK_ARTWORK_SIZE))
                .placeholder(R.drawable.audioplayer_placeholder)
                .transform(RoundedCorners(TrackViewHolder.ROUNDED_CORNER_RADIUS))
                .into(binding.artworkUrl512)
        } else {
            binding.artworkUrl512.setImageResource(R.drawable.audioplayer_placeholder)
        }

        if (track.trackTimeMillis != null) {
            binding.trackTimeMills.text = SimpleDateFormat(
                "mm:ss", Locale.getDefault()
            ).format(track.trackTimeMillis)
        } else {
            binding.trackTimeMills.text = getString(R.string.default_trackTimeMills)
        }

        if (track.collectionName != null) {
            binding.collectionNameSubj.isVisible = true
            binding.collectionName.isVisible = true
            binding.collectionName.text = track.collectionName
        } else {
            binding.collectionNameSubj.isVisible = false
            binding.collectionName.isVisible = false
        }

        val year = track.releaseDate?.substring(0..3)
        binding.releaseDate.text = year
        binding.primaryGenreName.text = track.primaryGenreName
        binding.country.text = track.country

        viewModel.playerState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                PlayerState.PLAYING -> startPlayer()
                PlayerState.PAUSED -> pausePlayer()
                PlayerState.PREPARED -> {
                    binding.playIc.isEnabled = true
                    binding.playIc.setImageResource(R.drawable.play)
                    binding.timer.text = viewModel.startTime()
                }


                is PlayerState.CurrentPosition -> binding.timer.text = state.time

                else -> {}
            }
        })

        preparePlayer(track.previewUrl)

        viewModel.observeFavoritesState().observe(viewLifecycleOwner) {
            renderLikeButton(it)
        }
        viewModel.observeTrackInPlaylistState().observe(viewLifecycleOwner) {
            makeToast(it)
        }
        viewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            adapter?.playlist?.clear()
            adapter?.playlist?.addAll(playlist)
            adapter?.notifyDataSetChanged()
        }

        adapter?.itemClickListener = { position, tracksId, playlist ->
            viewModel.addTracksIdInPlaylist(playlist, tracksId, track)
        }
        binding.createPlaylist.setOnClickListener {
            val action =
                AudioPlayerFragmentDirections.actionAudioPlayerFragmentToNewPlaylistFragment()
            findNavController().navigate(action)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.releasePlayer()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.playerState.value == PlayerState.PREPARED) {
            viewModel.preparePlayer(track.previewUrl, track)
            viewModel.startTime()

        }

    }

    private fun preparePlayer(previewUrl: String?) {
        viewModel.preparePlayer(previewUrl, track)

    }

    private fun startPlayer() {
        binding.playIc.setImageResource(R.drawable.pause)

    }

    private fun pausePlayer() {
        binding.playIc.setImageResource(R.drawable.play)

    }

    private fun renderLikeButton(isFavorite: Boolean) {
        val imageResource = if (isFavorite) {
            R.drawable.favorite
        } else {
            R.drawable.unfavorite
        }
        binding.favorite.setImageResource(imageResource)
    }

    private fun showBottomSheet() {
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun makeToast(state: TrackInPlaylistState) {
        when (state) {
            is TrackInPlaylistState.TrackIsAlreadyInPlaylist -> Toast.makeText(
                requireContext(),
                getString(R.string.track_already_in_playlist) + " ${state.playlistName}",
                Toast.LENGTH_SHORT
            ).show()

            is TrackInPlaylistState.TrackAddToPlaylist -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.track_added) + " ${state.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            }

            else -> {}
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        return formatter.format(milliseconds)
    }
}