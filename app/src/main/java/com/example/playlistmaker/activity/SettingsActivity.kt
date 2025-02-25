package com.example.playlistmaker.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnShareApp = findViewById<TextView>(R.id.share_app)
        btnShareApp.setOnClickListener {
            val sherAppIntent = Intent(Intent.ACTION_SEND)
            sherAppIntent.type = "text/plain"
            sherAppIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.url__share_app))
            val chosenIntent = Intent.createChooser(
                sherAppIntent,
                getString(R.string.url__share_app_hint)
            )

            startActivity(chosenIntent)
        }

        val btnSupport = findViewById<TextView>(R.id.support)
        btnSupport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:")
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.developer_email)))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_support_subject))
            emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_support_text))

            startActivity(emailIntent);
        }

        val btnUserAgreement = findViewById<TextView>(R.id.user_agreement)
        btnUserAgreement.setOnClickListener {
            val browseIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.url__user_agreement))
            )
            startActivity(browseIntent)
        }
    }
}