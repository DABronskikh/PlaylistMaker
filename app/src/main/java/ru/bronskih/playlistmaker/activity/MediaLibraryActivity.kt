package ru.bronskih.playlistmaker.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.bronskih.playlistmaker.R

class MediaLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_library)
    }
}