package com.practicum.playlistmaker.mediateka.ui

import TrackAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import com.practicum.playlistmaker.mediateka.ui.FragmentFavorites.Companion.CLICK_DEBOUNCE_DELAY
import com.practicum.playlistmaker.mediateka.ui.viewmodel.PlaylistInfoViewModel
import com.practicum.playlistmaker.mediateka.ui.viewmodel.models.TracksInPlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.TrackWordConvertor
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistInfoFragment : Fragment() {

    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!

    private val args: PlaylistInfoFragmentArgs by navArgs()

    private val playlistInfoViewModel: PlaylistInfoViewModel by viewModel<PlaylistInfoViewModel>()

    private lateinit var openPlayerDebounce: (Track) -> Unit

    private var adapter: TrackAdapter? = null

    private lateinit var playlistBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var playlist: Playlist? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistInfoViewModel.getPlaylistById(args.playlist.playlistId ?: 0)
        initObservers()
        initBottomSheet()

        adapter = TrackAdapter(object : TrackAdapter.OnItemClickListener {
            override fun onItemClick(track: Track) {
                openPlayerDebounce(track)
            }
        }).apply {
            onLongClickListener = { clickedTrack ->
                showConfirmDeleteTrack(clickedTrack)
            }
        }
        binding.recyclerViewInPlaylist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewInPlaylist.adapter = adapter

        binding.backButton.setOnClickListener() {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })
        openPlayerDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { clickedTrack ->
            val action =
                PlaylistInfoFragmentDirections.actionPlaylistInfoFragmentToAudioPlayerFragment(
                    clickedTrack
                )
            findNavController().navigate(action)
        }

        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.menuShare.setOnClickListener {
            sharePlaylist()
        }

        with(binding) {
            menuButton.setOnClickListener {
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                menuBottomSheet.isVisible = true

                menuBottomSheetBehavior.addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        when (newState) {
                            BottomSheetBehavior.STATE_HIDDEN -> {
                                playlistOverlay.visibility = View.GONE
                            }
                            else -> {
                                playlistOverlay.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                })
            }
        }
        binding.menuDelete.setOnClickListener {
            if (playlist != null) {
                showConfirmDeletePlaylist(playlist!!)
            }
        }
        binding.menuEdit.setOnClickListener {
            if (playlist != null) {
                val action =
                    PlaylistInfoFragmentDirections.actionPlaylistInfoFragmentToPlaylistEditFragment(
                        playlist!!
                    )
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewInPlaylist.adapter = null
        _binding = null
        adapter = null
    }

    private fun setPlaylistData(playlist: Playlist) {
        with(binding) {
            playlistName.text = playlist.playlistName
            playlistDescription.text = playlist.playlistDescription
            setImageResource(playlistCover, playlist)
            setTracksCount(tracksCount, playlist)
        }
    }

    private fun setMenuData(playlist: Playlist) {
        with(binding) {
            briefInfo.playlistNameBottomSheet.text = playlist.playlistName
            setImageResource(briefInfo.bottomSheetTrackCover, playlist)
            setTracksCount(briefInfo.tracksCountBottomSheet, playlist)
        }
    }

    private fun setImageResource(imageView: ImageView, playlist: Playlist) {
        with(binding) {
            if (playlist.uri.isNullOrEmpty()) {
                imageView.setImageResource(R.drawable.placeholder)
            } else {
                imageView.setImageURI(playlist.uri.toUri())
            }
        }
    }

    private fun setTracksCount(textView: TextView, playlist: Playlist) {
        textView.text = binding.root.context.getString(
            R.string.track_count_format,
            playlist.tracksCount,
            TrackWordConvertor.getTrackWord(playlist.tracksCount)
        )
    }

    fun initBottomSheet() {
        with(binding) {
            playlistBottomSheetBehavior = BottomSheetBehavior.from(playlistBottomSheet).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED

                menuBottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheet).apply {
                    state = BottomSheetBehavior.STATE_COLLAPSED
                    menuBottomSheet.isVisible = false
                }
            }

            menuButton.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val screenHeight = resources.displayMetrics.heightPixels

                    val menuPosition = IntArray(2)
                    menuButton.getLocationOnScreen(menuPosition)

                    val marginInPx = resources.getDimensionPixelSize(R.dimen.margin24)
                    val menuBottomY = menuPosition[1] + marginInPx

                    val bottomSheetPeekHeight = screenHeight - menuBottomY

                    playlistBottomSheetBehavior.peekHeight = bottomSheetPeekHeight
                    menuButton.viewTreeObserver.removeOnGlobalLayoutListener(this)

                }
            })

            playlistName.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val screenHeight = resources.displayMetrics.heightPixels
                    val playlistNamePosition = IntArray(2)
                    playlistName.getLocationOnScreen(playlistNamePosition)
                    val playlistNameBottomY = playlistNamePosition[1]
                    val bottomSheetPeekHeight = screenHeight - playlistNameBottomY

                    menuBottomSheetBehavior.peekHeight = bottomSheetPeekHeight
                    playlistName.viewTreeObserver.removeOnGlobalLayoutListener(this)

                }
            })
        }
    }

    private fun initObservers() {
        playlistInfoViewModel.observeStatePlaylist().observe(viewLifecycleOwner) { playlist ->
            this.playlist = playlist
            setPlaylistData(playlist)
            setMenuData(playlist)
        }

        playlistInfoViewModel.observeStatePlaylist().observe(viewLifecycleOwner) {
            playlistInfoViewModel.observeStateRenderTracksLiveData().observe(viewLifecycleOwner) {
                render(it)
            }
        }

        playlistInfoViewModel.observeStateTracksDuration().observe(viewLifecycleOwner) { duration ->
                binding.playlistDuration.text = duration
            }
    }

    private fun render(state: TracksInPlaylistState) {
        when (state) {
            is TracksInPlaylistState.Content -> showContent(state.tracksInPlaylist)
            is TracksInPlaylistState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        with(binding) {
            recyclerViewInPlaylist.isVisible = false
            placeholderMessage.isVisible = true
            placeholderMessage.setText(R.string.playlist_empty)
        }
    }

    private fun showContent(tracks: List<Track>) {
        with(binding) {
            recyclerViewInPlaylist.isVisible = true
            placeholderMessage.isVisible = false
        }
        adapter?.trackList?.clear()
        adapter?.trackList?.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }


    private fun showConfirmDeleteTrack(track: Track) {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.CustomAlertDialogTheme
        ).setTitle(R.string.delete_track).setMessage(R.string.delete_track_alert)
            .setNeutralButton(R.string.cancel) { _, _ -> }
            .setPositiveButton(R.string.delete) { _, _ ->
                playlistInfoViewModel.deleteTracksInPlaylist(track.trackId)
            }.show()

    }

    private fun showConfirmDeletePlaylist(playlist: Playlist) {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.CustomAlertDialogTheme
        ).setTitle("${getString(R.string.playlist_delete_alert)} \"${playlist.playlistName}\"?")
            .setNeutralButton(R.string.no) { _, _ -> }.setPositiveButton(R.string.yes) { _, _ ->
                playlistInfoViewModel.deletePlaylist()
                findNavController().navigateUp()
            }.show()
    }

    private fun sharePlaylist() {
        playlistInfoViewModel.observeStateTracksInPlaylist().observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(), R.string.no_tracks_to_share, Toast.LENGTH_SHORT
                ).show()
            } else {
                playlistInfoViewModel.sharePlaylist()
            }
        }
    }
}