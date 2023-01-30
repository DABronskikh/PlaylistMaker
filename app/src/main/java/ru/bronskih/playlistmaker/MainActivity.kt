package ru.bronskih.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val btnLibrary = findViewById<Button>(R.id.btnLibrary)
        val btnSettings = findViewById<Button>(R.id.btnSettings)

        val searchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val displaySearch = Intent(this@MainActivity, MediaLibraryActivity::class.java)
                startActivity(displaySearch)
            }
        }

        btnSearch.setOnClickListener(searchClickListener)

        btnLibrary.setOnClickListener {
            val displayMediaLibrary = Intent(this, MediaLibraryActivity::class.java)
            startActivity(displayMediaLibrary)
        }

        btnSettings.setOnClickListener {
            val displaySettings = Intent(this, SettingsActivity::class.java)
            startActivity(displaySettings)
        }
    }
}
