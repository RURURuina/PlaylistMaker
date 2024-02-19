package com.practicum.playlistmaker

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private lateinit var  adapter: TrackAdapter
    private lateinit var  inputEditText: EditText
    private var inputValue : String = ""

    companion object {
        private const val INPUT_VALUE_KEY = "input_value"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<LinearLayout>(R.id.back_button)
        inputEditText = findViewById(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        backButton.setOnClickListener {
            finish()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val trackNames = resources.getStringArray(R.array.track_names)
        val artistNames = resources.getStringArray(R.array.artist_names)
        val trackDurations = resources.getStringArray(R.array.track_durations)
        val artworkUrl100 = resources.getStringArray(R.array.artworkUrl100)

        val trackList = mutableListOf<Track>()

        for (i in trackNames.indices) {
            val track = Track(trackNames[i], artistNames[i], trackDurations[i], artworkUrl100[i])
            trackList.add(track)
        }

        adapter = TrackAdapter(trackList)
        recyclerView.adapter = adapter

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputValue = s?.toString() ?:""
                clearButton.visibility = clearButtonVisibility(s)

            }

            override fun afterTextChanged(s: Editable?) {

            }

            private fun clearButtonVisibility (s: CharSequence?) : Int =  if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

        }
        inputEditText.addTextChangedListener(textWatcher)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_VALUE_KEY, inputValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputValue = savedInstanceState.getString(INPUT_VALUE_KEY, "")
        inputEditText.setText(inputValue)
    }
}