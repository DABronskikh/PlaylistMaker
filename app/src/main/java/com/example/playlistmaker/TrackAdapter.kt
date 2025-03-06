package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.data.Track
import com.example.playlistmaker.service.SearchHistoryService

class TrackAdapter(
    private val clickListener: OnInteractionListener
) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()
        set(newTrackList) {
            field = newTrackList
            this.notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }


    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            clickListener.onClick(tracks[position])
        }
    }

    fun interface OnInteractionListener {
        fun onClick(track: Track)
    }
}
