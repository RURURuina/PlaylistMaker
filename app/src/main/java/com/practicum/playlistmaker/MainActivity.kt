package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        val mediatekaButton = findViewById<Button>(R.id.mediateka_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        val searchButtonClickListener : View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Какой-то текст", Toast.LENGTH_SHORT)
            }
        }
        mediatekaButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Какой-то текст2", Toast.LENGTH_SHORT).show()
        }

        settingsButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Какой-то текст3", Toast.LENGTH_SHORT).show()
        }

        searchButton.setOnClickListener(searchButtonClickListener)
    }
}