package ru.bronskih.playlistmaker.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import ru.bronskih.playlistmaker.R
import ru.bronskih.playlistmaker.util.AndroidUtils

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_QUERY = "SEARCH_QUERY"
    }

    var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val toolbar = findViewById<Toolbar>(R.id.searchToolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            AndroidUtils.hideKeyboard(it)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchQuery = p0.toString()
                clearButton.visibility = clearButtonVisibility(p0)
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(SEARCH_QUERY, searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        searchQuery = savedInstanceState.getString(SEARCH_QUERY, "")
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        inputEditText.setText(searchQuery)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}