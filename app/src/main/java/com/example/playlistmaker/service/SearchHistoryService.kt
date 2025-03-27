package com.example.playlistmaker.service

import android.content.SharedPreferences
import com.example.playlistmaker.data.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryService(private val sharedPrefs: SharedPreferences) {

    companion object {
        const val SEARCH_HISTORY_TRACK_LIST_KEY = "track_list_key"
        const val SEARCH_HISTORY_LIST_SIZE = 10
    }

    fun getHistory(): ArrayList<Track> {
        return read()
    }

    fun add(track: Track) {
        val allTracks = read()
        allTracks.remove(track)
        allTracks.add(0, track)
        allTracks.take(SEARCH_HISTORY_LIST_SIZE)

        write(allTracks)
    }

    fun clear() {
        sharedPrefs.edit()
            .remove(SEARCH_HISTORY_TRACK_LIST_KEY)
            .apply()
    }

    private fun read(): ArrayList<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_TRACK_LIST_KEY, null)
            ?: return arrayListOf()
        return Gson().fromJson(json, object : TypeToken<ArrayList<Track>>() {}.type)

    }

    private fun write(tracks: MutableList<Track>) {
        val json = Gson().toJson(tracks)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_TRACK_LIST_KEY, json)
            .apply()
    }

}
