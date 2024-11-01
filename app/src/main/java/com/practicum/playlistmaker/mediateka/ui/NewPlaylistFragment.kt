package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.mediateka.ui.viewmodel.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

open class NewPlaylistFragment : Fragment() {

    open val newPlaylistViewModel by viewModel<NewPlaylistViewModel>()



    private lateinit var  toastPlaylistName: String

    lateinit var pickVisualMedia: ActivityResultLauncher<PickVisualMediaRequest>

    lateinit var alertDialog: MaterialAlertDialogBuilder

    var isImageAdd: Boolean = false

    private var _binding: FragmentNewPlaylistBinding? = null
    val binding get() = _binding!!

    open lateinit var  playlistNameEditText: TextInputEditText

    open lateinit var playlistDescriptionEditText: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)

        requireActivity().window
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEditText()

        playlistNameEditText.doOnTextChanged { text, _, _, _ ->
            binding.createPlaylistButton.isEnabled = !text.isNullOrBlank()
            newPlaylistViewModel.playlistName = text.toString()
            toastPlaylistName = text.toString()
        }

        playlistDescriptionEditText.doOnTextChanged {text, _, _, _ ->
            newPlaylistViewModel.playlistDescription = text.toString()
        }

        pickVisualMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {uri ->
            if (uri != null) {
                binding.newPlaylistIc.setImageURI(uri)
                val savedUri = newPlaylistViewModel.saveImageToLocalStorage(uri)
                newPlaylistViewModel.uri = savedUri
                isImageAdd = true
            }
        }

        alertDialog = MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogTheme)
            .setTitle(R.string.creating_playlist_back)
            .setMessage(R.string.creating_playlist_loss_data)
            .setNeutralButton(R.string.cancel) { _, _ ->}
            .setPositiveButton(R.string.finish) { _, _ ->
                findNavController().navigateUp()
            }

        binding.newPlaylistIc.setOnClickListener {
            pickVisualMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.backButton.setOnClickListener {
            onBackPressed(playlistNameEditText, playlistDescriptionEditText)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed(playlistNameEditText, playlistDescriptionEditText)
                }
            }
        )
        binding.createPlaylistButton.setOnClickListener {
            newPlaylistViewModel.createPlaylist()

            findNavController().navigateUp()
            Toast.makeText(
                requireContext(),
                "Плейлист $toastPlaylistName создан",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initEditText() {
        playlistNameEditText =
            binding.playlistName.findViewById(R.id.playlistName)

        playlistDescriptionEditText =
            binding.playlistDescription.findViewById(R.id.playlistDescription)
    }

    open fun onBackPressed(
        playlistNameEditText: TextInputEditText,
        playlistDescriptionEditText: TextInputEditText
    ) {
        if (isImageAdd || !playlistNameEditText.text.isNullOrBlank() || !playlistDescriptionEditText.text.isNullOrBlank()) {
            alertDialog.show()
        } else {
            findNavController().navigateUp()
        }
    }
}