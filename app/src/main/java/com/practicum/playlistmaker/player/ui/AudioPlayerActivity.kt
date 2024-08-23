package com.practicum.playlistmaker.player.ui


import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.model.PlayerState
import com.practicum.playlistmaker.player.ui.view_model.MediaPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchFragment.Companion.AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.search.ui.adapters.TrackViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private val viewModel by viewModel<MediaPlayerViewModel>()


    private lateinit var playButton: ImageView
    private lateinit var timer: TextView
    private lateinit var track: Track

    companion object {
        private const val TRACK_ARTWORK_SIZE = "512x512bb.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        track = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(AUDIO_PLAYER_KEY, Track::class.java) as Track
        } else {
            intent.getSerializableExtra(AUDIO_PLAYER_KEY) as Track
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


        playButton.setOnClickListener { viewModel.playbackControl() }


        trackName.text = track.trackName
        artistName.text = track.artistName


        if (track.artworkUrl100?.isNotEmpty() == true) {
            Glide.with(this).load(track.artworkUrl100!!.replaceAfterLast("/", TRACK_ARTWORK_SIZE))
                .placeholder(R.drawable.audioplayer_placeholder)
                .transform(RoundedCorners(TrackViewHolder.ROUNDED_CORNER_RADIUS)).into(artwork)
        } else {
            artwork.setImageResource(R.drawable.audioplayer_placeholder)
        }

        if (track.trackTimeMillis != null) {
            trackTimeMills.text = SimpleDateFormat(
                "mm:ss", Locale.getDefault()
            ).format(track.trackTimeMillis)
        } else {
            trackTimeMills.text = getString(R.string.default_trackTimeMills)
        }

        if (track.collectionName != null) {
            collectionNameSubj.isVisible = true
            collectionName.isVisible = true
            collectionName.text = track.collectionName
        } else {
            collectionNameSubj.isVisible = false
            collectionName.isVisible = false
        }

        val year = track.releaseDate?.substring(0..3)
        releaseDate.text = year
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        viewModel.playerState.observe(this, Observer { state ->
            when (state) {
                PlayerState.PLAYING -> startPlayer()
                PlayerState.PAUSED -> pausePlayer()
                PlayerState.PREPARED -> {
                    playButton.isEnabled = true
                    playButton.setImageResource(R.drawable.play)
                    timer.text = viewModel.startTime()
                }

                is PlayerState.CurrentPosition -> timer.text = state.time

                else -> {}
            }
        })

        preparePlayer(track.previewUrl)


    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.playerState.value == PlayerState.PREPARED) {
            viewModel.preparePlayer(track.previewUrl)
            viewModel.startPlayer()
        }

    }

    private fun preparePlayer(previewUrl: String?) {
        viewModel.preparePlayer(previewUrl)

    }

    private fun startPlayer() {
        playButton.setImageResource(R.drawable.pause)

    }

    private fun pausePlayer() {
        playButton.setImageResource(R.drawable.play)

    }

    private fun formatTime(milliseconds: Int): String {
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        return formatter.format(milliseconds)
    }
}