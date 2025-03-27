package com.example.playlistmaker.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.data.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        const val INTENT_TRACK = "TRACK"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.activity_track__toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val track = Gson().fromJson(intent.getStringExtra(INTENT_TRACK), Track::class.java)

        val trackCover: ImageView = findViewById(R.id.activity_track__cover)
        val trackName: TextView = findViewById(R.id.activity_track__name)
        val artistName: TextView = findViewById(R.id.activity_track__artist_name)
        val trackTime: TextView = findViewById(R.id.activity_track__trackTime)
        val collectionName: TextView = findViewById(R.id.activity_track__collection_name)
        val collection: TextView = findViewById(R.id.activity_track__collection)
        val releaseDate: TextView = findViewById(R.id.activity_track__release_date)
        val primaryGenreName: TextView = findViewById(R.id.activity_track__primary_genre_name)
        val country: TextView = findViewById(R.id.activity_track__country_data)
        val progress: TextView = findViewById(R.id.activity_track__progress)

        Glide
            .with(trackCover)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_album_360)
            .error(R.drawable.ic_album_360)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(trackCover)

        progress.text = "0.00"
        trackName.text = track.trackName
        artistName.text = track.artistName
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTimeMillis)

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .parse(track.releaseDate)
        releaseDate.text = date?.let {
            SimpleDateFormat("yyyy", Locale.getDefault()).format(it)
        }

        if (track.collectionName.isNotEmpty()) {
            collectionName.text = track.collectionName
        } else {
            collectionName.visibility = View.GONE
            collection.visibility = View.GONE
        }

    }
}
