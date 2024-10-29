package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import com.practicum.playlistmaker.mediateka.ui.viewmodel.PlaylistEditViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistEditFragment : NewPlaylistFragment() {

    private val args: PlaylistEditFragmentArgs by navArgs()

    private val playlistEditViewModel: PlaylistEditViewModel by viewModel() {
        parametersOf(args.playlist)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistEditViewModel.getPlaylistById()
        playlistEditViewModel.observeStatePlaylist().observe(viewLifecycleOwner) { playlist ->
            setPreviousPlaylistData(playlist)
        }
        with(binding) {
            playlistsHeaderText.text = getString(R.string.editPlaylist)
            createPlaylistButton.text = getString(R.string.saveChanges)
        }

        playlistNameEditText.doOnTextChanged { text, _, _, _ ->
            binding.createPlaylistButton.isEnabled = !text.isNullOrBlank()
            playlistEditViewModel.playlistName = text.toString()
        }

        playlistDescriptionEditText.doOnTextChanged { text, _, _, _ ->
            playlistEditViewModel.playlistDescription = text.toString()
        }

        pickVisualMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.newPlaylistIc.setImageURI(uri)
                    val savedUri = playlistEditViewModel.saveImageToLocalStorage(uri)
                    playlistEditViewModel.uri = savedUri
                    isImageAdd = true
                }
            }
        //Почему-то не дублируя этот код сверху, плейлист не хотел редактироваться, так что костыль

        binding.createPlaylistButton.setOnClickListener() {
            playlistEditViewModel.editPlaylist()
            findNavController().navigateUp()
            Toast.makeText(requireContext(), "Изменения сохранены", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onBackPressed(
        playlistNameEditText: TextInputEditText, playlistDescriptionEditText: TextInputEditText
    ) {
        findNavController().navigateUp()
    }

    private fun setPreviousPlaylistData(playlist: Playlist) {
        playlistNameEditText?.setText(playlist.playlistName)
        playlistDescriptionEditText?.setText(playlist.playlistDescription)
        if (playlist.uri.isNullOrEmpty()) {
            binding.newPlaylistIc.setImageResource(R.drawable.placeholder)
        } else {
            binding.newPlaylistIc.setImageURI(playlist.uri.toUri())
        }
    }
}