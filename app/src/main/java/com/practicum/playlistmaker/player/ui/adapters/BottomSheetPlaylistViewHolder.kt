package com.practicum.playlistmaker.player.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.BottomSheetViewBinding
import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import com.practicum.playlistmaker.utils.TrackWordConvertor

class BottomSheetPlaylistViewHolder(private val binding: BottomSheetViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist, clickListener: ((Int, List<Long>, Playlist) -> Unit)?) {
        with(binding) {
            playlistNameBottomSheet.text = playlist.playlistName
            tracksCountBottomSheet.text =
                playlist.tracksCount.toString() + " " + TrackWordConvertor.getTrackWord(playlist.tracksCount)

        }
        Glide.with(itemView).load(playlist.uri).centerCrop().placeholder(R.drawable.placeholder)
            .into(binding.bottomSheetTrackCover)

        binding.root.setOnClickListener {
            clickListener?.invoke(adapterPosition, playlist.tracksIdInPlaylist, playlist)
        }
    }
}