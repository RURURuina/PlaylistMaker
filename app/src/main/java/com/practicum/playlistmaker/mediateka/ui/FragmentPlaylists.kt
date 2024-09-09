package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import com.practicum.playlistmaker.mediateka.ui.adapters.PlaylistAdapter
import com.practicum.playlistmaker.mediateka.ui.viewmodel.PlaylistsViewModel
import com.practicum.playlistmaker.mediateka.ui.viewmodel.models.PlaylistState
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlaylists : Fragment() {

    private lateinit var binding: FragmentPlaylistsBinding
    private val playlistsViewModel: PlaylistsViewModel by viewModel<PlaylistsViewModel>()
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createPlaylist.setOnClickListener {
            val action = MediatekaFragmentDirections.actionMediatekaFragmentToNewPlaylistFragment()
            findNavController().navigate(action)
        }
        playlistAdapter = PlaylistAdapter()
        setupRecyclerView()

        playlistsViewModel.observeState().observe(viewLifecycleOwner, Observer { state ->
            render(state)
        })

    }

    private fun setupRecyclerView() {
        binding.playlistsRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = playlistAdapter
        }
    }

    private fun render(state: PlaylistState) {
        when(state) {
            is PlaylistState.Content -> showPlaylists(state.playlist)
            is PlaylistState.Empty -> showEmptyState()
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        with(binding) {

            playlistsRecyclerView.adapter?.notifyDataSetChanged()

            binding.playlistsRecyclerView.visibility = View.VISIBLE
            binding.playlistsPlaceholder.visibility = View.GONE
            binding.playlistPlaceholderMessage.visibility = View.GONE
        }
        playlistAdapter?.playlists?.clear()
        playlistAdapter?.playlists?.addAll(playlists)
        playlistAdapter?.notifyDataSetChanged()

    }

    private fun showEmptyState() {
        binding.playlistsRecyclerView.visibility = View.GONE
        binding.playlistsPlaceholder.visibility = View.VISIBLE
        binding.playlistPlaceholderMessage.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance() = FragmentPlaylists()
    }
}