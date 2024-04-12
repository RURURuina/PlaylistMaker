package com.practicum.playlistmaker

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.SearchActivity.Companion.AUDIO_PLAYER_KEY
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        val track =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                intent?.extras?.getParcelable(AUDIO_PLAYER_KEY, Track::class.java)
            } else {
                intent?.extras?.getParcelable(AUDIO_PLAYER_KEY)
            }

        val backButton = findViewById<Button>(R.id.back_button)
        val artwork = findViewById<ImageView>(R.id.artworkUrl512)
        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val playButton = findViewById<ImageView>(R.id.play_ic)
        val addToPlaylist = findViewById<ImageView>(R.id.add_to_playlist)
        val favoriteButton = findViewById<ImageView>(R.id.favorite)
        val trackTimeMills = findViewById<TextView>(R.id.trackTimeMills)
        val timer = findViewById<TextView>(R.id.timer)
        val collectionNameSubj = findViewById<TextView>(R.id.collectionName_subj)
        val collectionName = findViewById<TextView>(R.id.collectionName)
        val releaseDate = findViewById<TextView>(R.id.releaseDate)
        val primaryGenreName = findViewById<TextView>(R.id.primaryGenreName)
        val country = findViewById<TextView>(R.id.country)


        backButton.setOnClickListener {
            finish()
        }

        trackName.text = track?.trackName
        artistName.text = track?.artistName

        if (track?.artworkUrl100?.isNotEmpty() == true) {
            Glide.with(this)
                .load(track.artworkUrl512)
                .placeholder(R.drawable.audioplayer_placeholder)
                .transform(RoundedCorners(TrackViewHolder.ROUNDED_CORNER_RADIUS))
                .into(artwork)
        } else {
            artwork.setImageResource(R.drawable.audioplayer_placeholder)
        }

        if (track?.trackTimeMillis != null) {
            trackTimeMills.text = SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(track.trackTimeMillis)
        } else {
            trackTimeMills.text = getString(R.string.default_trackTimeMills)
        }

        if (track?.collectionName != null) {
            collectionNameSubj.isVisible = true
            collectionName.isVisible = true
            collectionName.text = track.collectionName
        } else {
            collectionNameSubj.isVisible = false
            collectionName.isVisible = false
        }

        val year = track?.releaseDate?.substring(0..3)
        releaseDate.text = year
        primaryGenreName.text = track?.primaryGenreName
        country.text = track?.country

    }
}