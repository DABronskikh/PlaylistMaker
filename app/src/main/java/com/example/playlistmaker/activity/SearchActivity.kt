package com.example.playlistmaker.activity

import android.content.Intent
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.TrackAdapter
import com.example.playlistmaker.data.Track
import com.example.playlistmaker.data.TracksResponse
import com.example.playlistmaker.service.RetrofitService
import com.example.playlistmaker.service.SearchHistoryService
import com.example.playlistmaker.util.AndroidUtils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_QUERY = "SEARCH_QUERY"
        const val SEARCH_HISTORY = "playlist_maker_search_history"
        const val SEARCH_HISTORY_TRACK_LIST_KEY = "track_list_key"
        const val INTENT_TRACK = "TRACK"
    }

    private var searchQuery = ""
    private lateinit var contentPlaceholderLayout: LinearLayout
    private lateinit var contentPlaceholderText: TextView
    private lateinit var contentPlaceholderImage: ImageView
    private lateinit var contentPlaceholderButton: Button
    private lateinit var recyclerViewTrackList: RecyclerView
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var searchHistoryButton: Button
    private lateinit var searchHistoryService: SearchHistoryService
    private lateinit var searchHistoryTrackList: RecyclerView
    private lateinit var listener: OnSharedPreferenceChangeListener

    private val trackAdapter = TrackAdapter { onClick(it) }
    private val searchHistoryTrackAdapter = TrackAdapter { onClick(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPreferencesSearchHistory =
            getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE)
        searchHistoryService =
            SearchHistoryService(sharedPreferencesSearchHistory)
        searchHistoryTrackAdapter.tracks = searchHistoryService.getHistory()


        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val inputEditText = findViewById<EditText>(R.id.input_edit_text)
        val clearButton = findViewById<ImageView>(R.id.clear_icon)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            AndroidUtils.hideKeyboard(it)
            recyclerViewTrackList.visibility = View.GONE
            hidePlaceholder()
        }

        searchHistoryLayout = findViewById(R.id.search_history)
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryLayout.visibility =
                if (hasFocus
                    && searchHistoryTrackAdapter.tracks.isNotEmpty()
                ) View.VISIBLE else View.GONE
        }
        inputEditText.requestFocus()

        contentPlaceholderLayout = findViewById(R.id.content_placeholder)
        contentPlaceholderText = findViewById(R.id.content_placeholder__text)
        contentPlaceholderImage = findViewById(R.id.content_placeholder__image)
        contentPlaceholderButton = findViewById(R.id.content_placeholder__button)

        contentPlaceholderButton.setOnClickListener {
            searchTracks(inputEditText.text.toString())
        }

        searchHistoryButton = findViewById(R.id.search_history__button)
        searchHistoryButton.setOnClickListener {
            searchHistoryService.clear()
            searchHistoryLayout.visibility = View.GONE
        }

        recyclerViewTrackList = findViewById(R.id.recycler_view_track_list)
        recyclerViewTrackList.layoutManager = LinearLayoutManager(this)

        recyclerViewTrackList.adapter = trackAdapter

        searchHistoryTrackList = findViewById(R.id.search_history__track_list)
        searchHistoryTrackList.layoutManager = LinearLayoutManager(this)

        searchHistoryTrackList.adapter = searchHistoryTrackAdapter

        listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == SEARCH_HISTORY_TRACK_LIST_KEY) {
                searchHistoryTrackAdapter.tracks = searchHistoryService.getHistory()
            }
        }

        sharedPreferencesSearchHistory.registerOnSharedPreferenceChangeListener(listener)

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchQuery = p0.toString()
                clearButton.visibility = clearButtonVisibility(p0)

                if (inputEditText.hasFocus() && p0?.isEmpty() == true && searchHistoryTrackAdapter.tracks.isNotEmpty()) {
                    searchHistoryLayout.visibility = View.VISIBLE
                    recyclerViewTrackList.visibility = View.GONE
                    hidePlaceholder()
                    trackAdapter.tracks = ArrayList()
                } else {
                    searchHistoryLayout.visibility = View.GONE
                    recyclerViewTrackList.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks(inputEditText.text.toString())
            }
            false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(SEARCH_QUERY, searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        searchQuery = savedInstanceState.getString(SEARCH_QUERY, "")
        val inputEditText = findViewById<EditText>(R.id.input_edit_text)
        inputEditText.setText(searchQuery)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun searchTracks(text: String) {
        RetrofitService
            .getTrackApiService()
            .search(text)
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.isSuccessful) {
                        val tracksJson = response.body()?.results

                        if (!tracksJson.isNullOrEmpty()) {
                            trackAdapter.tracks = tracksJson as ArrayList<Track>
                            hidePlaceholder()
                            recyclerViewTrackList.visibility = View.VISIBLE
                        } else {
                            recyclerViewTrackList.visibility = View.GONE
                            searchHistoryLayout.visibility = View.GONE
                            showPlaceholder(
                                getString(R.string.search__placeholder_no_results),
                                R.drawable.ic_search_no_results_120
                            )
                        }
                    } else {
                        recyclerViewTrackList.visibility = View.GONE
                        searchHistoryLayout.visibility = View.GONE
                        showPlaceholder(
                            getString(R.string.search__placeholder_error_internet),
                            R.drawable.ic_error_internet_120,
                            true
                        )
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    recyclerViewTrackList.visibility = View.GONE
                    searchHistoryLayout.visibility = View.GONE
                    showPlaceholder(
                        getString(R.string.search__placeholder_error_internet),
                        R.drawable.ic_error_internet_120,
                        true
                    )
                    t.printStackTrace()
                }
            })
    }

    private fun showPlaceholder(text: String, imageResId: Int, showActionButton: Boolean = false) {
        contentPlaceholderLayout.visibility = View.VISIBLE
        contentPlaceholderText.text = text
        contentPlaceholderImage.setImageResource(imageResId)
        contentPlaceholderButton.visibility = if (showActionButton) View.VISIBLE else View.GONE
    }

    private fun hidePlaceholder() {
        contentPlaceholderLayout.visibility = View.GONE
    }

    private fun onClick(track: Track) {
        searchHistoryService.add(track)

        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra(INTENT_TRACK, Gson().toJson(track))
        }
        startActivity(intent)
    }

}
