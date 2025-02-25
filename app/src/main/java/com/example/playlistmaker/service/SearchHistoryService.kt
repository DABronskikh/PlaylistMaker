package com.example.playlistmaker.service

import android.content.SharedPreferences
import com.example.playlistmaker.Constants
import com.example.playlistmaker.data.Track
import com.google.gson.Gson

class SearchHistoryService(private val sharedPrefs: SharedPreferences) {

    fun getHistory(): MutableList<Track> {
        return read()
    }

    fun add(track: Track) {
        val allTracks = mutableListOf<Track>()
        allTracks.add(track)
        allTracks.addAll(read())

        val newTrackList = allTracks
            .filterIndexed() { idx, it -> idx == 0 || it.trackId != track.trackId }
            .take(Constants.SEARCH_HISTORY_LIST_SIZE)

        write(newTrackList)
    }

    fun clear() {
        sharedPrefs.edit()
            .remove(Constants.SEARCH_HISTORY_TRACK_LIST_KEY)
            .apply()
    }

    private fun read(): MutableList<Track> {
        val json = sharedPrefs.getString(Constants.SEARCH_HISTORY_TRACK_LIST_KEY, null)
            ?: return mutableListOf()
        return Gson().fromJson(json, Array<Track>::class.java).toMutableList()
    }

    private fun write(tracks: List<Track>) {
        val json = Gson().toJson(tracks)
        sharedPrefs.edit()
            .putString(Constants.SEARCH_HISTORY_TRACK_LIST_KEY, json)
            .apply()
    }

}
