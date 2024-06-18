package com.practicum.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.view_model.MediaPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchActivity.Companion.AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.search.ui.adapters.TrackViewHolder
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private val viewModel: MediaPlayerViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MediaPlayerViewModel::class.java)) {
                    return MediaPlayerViewModel(Creator.mediaPlayerCreator()) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }


    private lateinit var playButton: ImageView
    private lateinit var timer: TextView
    private val handler = Handler(Looper.getMainLooper())

    private val updateTimer = object : Runnable {
        override fun run() {
            if (viewModel.isPlaying()) {
                viewModel.updateCurrentPosition()
                handler.postDelayed(this, 500)
            }
        }
    }

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
        playButton = findViewById(R.id.play_ic)
        val addToPlaylist = findViewById<ImageView>(R.id.add_to_playlist)
        val favoriteButton = findViewById<ImageView>(R.id.favorite)
        val trackTimeMills = findViewById<TextView>(R.id.trackTimeMills)
        timer = findViewById<TextView>(R.id.timer)
        val collectionNameSubj = findViewById<TextView>(R.id.collectionName_subj)
        val collectionName = findViewById<TextView>(R.id.collectionName)
        val releaseDate = findViewById<TextView>(R.id.releaseDate)
        val primaryGenreName = findViewById<TextView>(R.id.primaryGenreName)
        val country = findViewById<TextView>(R.id.country)



        backButton.setOnClickListener { finish() }
        playButton.setOnClickListener { playbackControl() }


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

        viewModel.playerState.observe(this, Observer { state ->
            when (state) {
                PlayerState.PLAYING -> startPlayer()
                PlayerState.PAUSED -> pausePlayer()
                PlayerState.PREPARED -> {
                    playButton.isEnabled = true
                    playButton.setImageResource(R.drawable.play)
                }

                PlayerState.DEFAULT -> {
                    playButton.isEnabled = false
                }
            }
        })

        viewModel.currentPosition.observe(this, Observer { position ->
            timer.text = formatTime(position)
        })

        preparePlayer(track?.previewUrl)

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimer)
        viewModel.releasePlayer()
    }

    private fun preparePlayer(previewUrl: String?) {
        viewModel.preparePlayer(previewUrl)
    }


    private fun startPlayer() {
        playButton.setImageResource(R.drawable.pause)
        handler.post(updateTimer)
    }

    private fun pausePlayer() {
        playButton.setImageResource(R.drawable.play)
        handler.removeCallbacks(updateTimer)
    }

    private fun playbackControl() {
        when (viewModel.playerState.value) {
            PlayerState.PLAYING -> viewModel.pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> viewModel.startPlayer()
            else -> Unit
        }
    }


    private fun formatTime(milliseconds: Int): String {
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        return formatter.format(milliseconds)
    }
}