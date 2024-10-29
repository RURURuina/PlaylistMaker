package com.practicum.playlistmaker.player.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.BottomSheetViewBinding
import com.practicum.playlistmaker.mediateka.domain.model.Playlist

class BottomSheetPlaylistAdapter() : RecyclerView.Adapter<BottomSheetPlaylistViewHolder>() {

    var itemClickListener: ((Int, List<Long>, Playlist) -> Unit)? = null
    val playlist: MutableList<Playlist> = mutableListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BottomSheetPlaylistViewHolder {
        val binding =
            BottomSheetViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BottomSheetPlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    override fun onBindViewHolder(holder: BottomSheetPlaylistViewHolder, position: Int) {
        holder.bind(playlist[position], itemClickListener)
    }
}