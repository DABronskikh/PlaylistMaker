package com.example.playlistmaker.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
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
        const val RELOAD_PROGRESS = 300L
        const val CURRENT_TIME_ZERO = "00:00"

        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private lateinit var toolbar: Toolbar
    private lateinit var trackCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var collectionName: TextView
    private lateinit var collection: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var progress: TextView
    private lateinit var btnPlayTrack: ImageButton

    private var mediaPlayer = MediaPlayer()
    private val play = R.drawable.ic_play_100
    private val pause = R.drawable.ic_pause_100
    private var playerState = STATE_DEFAULT
    private var mainHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initToolbar()
        initTrackInfo()
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.activity_track__toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initTrackInfo() {
        val track = Gson().fromJson(intent.getStringExtra(INTENT_TRACK), Track::class.java)

        trackCover = findViewById(R.id.activity_track__cover)
        trackName = findViewById(R.id.activity_track__name)
        artistName = findViewById(R.id.activity_track__artist_name)
        trackTime = findViewById(R.id.activity_track__trackTime)
        collectionName = findViewById(R.id.activity_track__collection_name)
        collection = findViewById(R.id.activity_track__collection)
        releaseDate = findViewById(R.id.activity_track__release_date)
        primaryGenreName = findViewById(R.id.activity_track__primary_genre_name)
        country = findViewById(R.id.activity_track__country_data)
        progress = findViewById(R.id.activity_track__progress)
        btnPlayTrack = findViewById(R.id.activity_track__btn_play_track)

        mainHandler = Handler(Looper.getMainLooper())

        Glide
            .with(trackCover)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_album_360)
            .error(R.drawable.ic_album_360)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(trackCover)

        progress.text = CURRENT_TIME_ZERO
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

        preparePlayer(track)

        btnPlayTrack.setOnClickListener {
            playbackControl()
            progressTimeControl()
        }

        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            mainHandler?.removeCallbacks(runThread)
            btnPlayTrack.setImageResource(play)
            progress.text = CURRENT_TIME_ZERO
        }
    }

    private val runThread = object : Runnable {
        override fun run() {
            progress.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)

            mainHandler?.postDelayed(
                this,
                RELOAD_PROGRESS
            )
        }
    }

    private fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            btnPlayTrack.setImageResource(play)
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            btnPlayTrack.setImageResource(play)
            playerState = STATE_PREPARED
        }
    }

    private fun progressTimeControl() {
        when (playerState) {
            STATE_PLAYING -> {
                mainHandler?.postDelayed(
                    runThread,
                    RELOAD_PROGRESS
                )
            }
            STATE_PAUSED -> {
                mainHandler?.removeCallbacks(runThread)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        btnPlayTrack.setImageResource(pause)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        btnPlayTrack.setImageResource(play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainHandler?.removeCallbacks(runThread)
    }
}
