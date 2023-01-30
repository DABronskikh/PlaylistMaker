package ru.bronskih.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnHome = findViewById<Button>(R.id.btnHome)

        btnHome.setOnClickListener {
            val displayMain = Intent(this, MainActivity::class.java)
            startActivity(displayMain)
        }

    }
}