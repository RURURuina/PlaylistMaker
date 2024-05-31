package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {

    private var trackList: MutableList<Track> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(trackList: MutableList<Track>) {
        this.trackList = trackList
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(track: Track)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList() {
        trackList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            listener.onItemClick(track)
        }
    }

    override fun getItemCount() = trackList.size
}
