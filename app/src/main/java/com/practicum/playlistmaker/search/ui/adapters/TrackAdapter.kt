import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.adapters.TrackViewHolder

class TrackAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var trackList: MutableList<Track?> = mutableListOf()

    var onLongClickListener: ((clickedTrack: Track) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(trackList: MutableList<Track?>) {
        Log.d("TrackAdapter", "Updating list with ${trackList.size} items")
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
        if (track != null) {
            holder.bind(track)
        }
        holder.itemView.setOnClickListener {
            if (track != null) {
                listener.onItemClick(track)
            }
        }
        holder.itemView.setOnLongClickListener {
            if (track != null) {
                onLongClickListener?.invoke(track)
            }
            true
        }
    }

    override fun getItemCount() = trackList.size
}