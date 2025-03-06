package com.example.playlistmaker.activity

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Constants

class App : Application() {

    var darkTheme = false
        private set

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(Constants.PLAYLIST_MAKER_SETTINGS, MODE_PRIVATE)

        darkTheme = sharedPrefs.getBoolean(Constants.DARK_THEME_KEY, isDarkModeOn())
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        sharedPrefs.edit().putBoolean(Constants.DARK_THEME_KEY, darkThemeEnabled).apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun isDarkModeOn(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        return isDarkModeOn
    }

}
