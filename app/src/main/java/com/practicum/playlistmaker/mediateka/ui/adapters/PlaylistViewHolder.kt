package com.practicum.playlistmaker.mediateka.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistItemBinding
import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import com.practicum.playlistmaker.utils.TrackWordConvertor

class PlaylistViewHolder(private val binding: PlaylistItemBinding):
    RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            with(binding) {
                playlistName.text = playlist.playlistName
                tracksCount.text = playlist.tracksCount.toString() + " " + TrackWordConvertor.getTrackCountString(playlist.tracksCount, itemView)
            }
            Glide.with(itemView).load(playlist.uri).centerCrop()
                .placeholder(R.drawable.placeholder).into(binding.playlistImage)
        }
}