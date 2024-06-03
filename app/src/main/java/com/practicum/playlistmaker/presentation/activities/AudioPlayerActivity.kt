package com.practicum.playlistmaker.presentation.activities

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.repositories.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.data.repositories.MediaPlayerRepository
import com.practicum.playlistmaker.domain.models.PlayerState
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.TrackViewHolder
import com.practicum.playlistmaker.presentation.activities.SearchActivity.Companion.AUDIO_PLAYER_KEY
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity() : AppCompatActivity() {

    private lateinit var mediaPlayerRepository: MediaPlayerRepository



    private var playerState = PlayerState.DEFAULT
    private lateinit var playButton: ImageView
    private lateinit var timer: TextView
    private val handler = Handler(Looper.getMainLooper())

    private val updateTimer = object : Runnable {
        override fun run() {
            if (mediaPlayerRepository.isPlaying()) {
                val currentPosition = mediaPlayerRepository.getCurrentPosition()
                timer.text = formatTime(currentPosition)
                handler.postDelayed(this, 500)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        val mediaPlayer = MediaPlayer()
        mediaPlayerRepository = MediaPlayerRepositoryImpl(mediaPlayer)

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

        preparePlayer(track?.previewUrl)

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

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimer)
        mediaPlayerRepository.releasePlayer()
    }

    private fun preparePlayer(previewUrl: String?) {
        mediaPlayerRepository.preparePlayer(previewUrl, {
            playButton.isEnabled = true
            playerState = PlayerState.PREPARED
        }, {
            onTimerComplete()
            playerState = PlayerState.PREPARED
        })
    }

    private fun onTimerComplete() {
        playButton.setImageResource(R.drawable.play)
        handler.removeCallbacks(updateTimer)
        timer.text = formatTime(0)

    }

    private fun startPlayer() {
        mediaPlayerRepository.startPlayer()
        playButton.setImageResource(R.drawable.pause)
        handler.post(updateTimer)
        playerState = PlayerState.PLAYING
    }

    private fun pausePlayer() {
        mediaPlayerRepository.pausePlayer()
        playButton.setImageResource(R.drawable.play)
        playerState = PlayerState.PAUSED
        handler.removeCallbacks(updateTimer)
    }

    private fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            else -> Unit
            }
        }


    private fun formatTime(milliseconds: Int): String {
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        return formatter.format(milliseconds)
    }
}