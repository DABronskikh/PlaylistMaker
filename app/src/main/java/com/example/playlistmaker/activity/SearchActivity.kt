package com.example.playlistmaker.activity

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
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.playlistmaker.R
import com.example.playlistmaker.TrackAdapter
import com.example.playlistmaker.TrackViewHolder
import com.example.playlistmaker.data.Track
import com.example.playlistmaker.data.TracksResponse
import com.example.playlistmaker.service.RetrofitService
import com.example.playlistmaker.service.TrackApiService
import com.example.playlistmaker.util.AndroidUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_QUERY = "SEARCH_QUERY"
    }

    private var searchQuery = ""
    private val tracklist: MutableList<Track> = mutableListOf()
    private lateinit var trackAdapter: Adapter<TrackViewHolder>
    private lateinit var contentPlaceholder: LinearLayout
    private lateinit var contentPlaceholderText: TextView
    private lateinit var contentPlaceholderImage: ImageView
    private lateinit var contentPlaceholderButton: Button
    private lateinit var recyclerViewTrackList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val inputEditText = findViewById<EditText>(R.id.input_edit_text)
        val clearButton = findViewById<ImageView>(R.id.clear_icon)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            AndroidUtils.hideKeyboard(it)

            tracklist.clear()
            recyclerViewTrackList.visibility = View.GONE
            hidePlaceholder()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchQuery = p0.toString()
                clearButton.visibility = clearButtonVisibility(p0)
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        contentPlaceholder = findViewById(R.id.content_placeholder)
        contentPlaceholderText = findViewById(R.id.content_placeholder__text)
        contentPlaceholderImage = findViewById(R.id.content_placeholder__image)
        contentPlaceholderButton = findViewById(R.id.content_placeholder__button)

        contentPlaceholderButton.setOnClickListener {
            searchTracks(inputEditText.text.toString())
        }

        recyclerViewTrackList = findViewById(R.id.recycler_view_track_list)
        recyclerViewTrackList.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(tracklist)
        recyclerViewTrackList.adapter = trackAdapter

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
                            tracklist.clear()
                            tracklist.addAll(tracksJson)
                            hidePlaceholder()
                            recyclerViewTrackList.visibility = View.VISIBLE
                            trackAdapter.notifyDataSetChanged()
                        } else {
                            recyclerViewTrackList.visibility = View.GONE
                            showPlaceholder(
                                getString(R.string.search__placeholder_no_results),
                                R.drawable.ic_search_no_results_120
                            )
                        }
                    } else {
                        recyclerViewTrackList.visibility = View.GONE
                        showPlaceholder(
                            getString(R.string.search__placeholder_error_internet),
                            R.drawable.ic_error_internet_120,
                            true
                        )
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    recyclerViewTrackList.visibility = View.GONE
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
        contentPlaceholder.visibility = View.VISIBLE
        contentPlaceholderText.text = text
        contentPlaceholderImage.setImageResource(imageResId)
        contentPlaceholderButton.visibility = if (showActionButton) View.VISIBLE else View.GONE
    }

    private fun hidePlaceholder() {
        contentPlaceholder.visibility = View.GONE
    }
}
