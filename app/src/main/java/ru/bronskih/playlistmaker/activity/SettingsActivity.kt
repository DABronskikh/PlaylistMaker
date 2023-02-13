package ru.bronskih.playlistmaker.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import ru.bronskih.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.settingsToolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val btnShareApp = findViewById<TextView>(R.id.shareApp)
        val btnSupport = findViewById<TextView>(R.id.support)
        val btnUserAgreement = findViewById<TextView>(R.id.userAgreement)

        btnShareApp.setOnClickListener {
            val sherAppIntent = Intent(Intent.ACTION_SEND)
            sherAppIntent.type = "text/plain"
            sherAppIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.url__share_app))
            val chosenIntent = Intent.createChooser(sherAppIntent, getString(R.string.url__share_app_hint));

            startActivity(chosenIntent)
        }

        btnSupport.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", getString(R.string.developer_email), null)
            );
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_support_subject));
            emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_support_text));

            startActivity(emailIntent);
        }

        btnUserAgreement.setOnClickListener {
            val browseIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.url__user_agreement))
            )
            startActivity(browseIntent)
        }

    }
}