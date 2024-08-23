package com.practicum.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.MediaPlayerRepository

class MediaPlayerRepositoryImpl(private var mediaPlayer: MediaPlayer?) : MediaPlayerRepository {

    override val playerCurrentPosition: Int
        get() = mediaPlayer?.currentPosition ?: 0

    override val playerDuration: Int
        get() = mediaPlayer?.duration ?: 0


    override fun preparePlayer(
        previewUrl: String?,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {

        if (previewUrl != null) {

            mediaPlayer = MediaPlayer().apply {

                setDataSource(previewUrl)
                prepareAsync()
                setOnPreparedListener { onPrepared.invoke() }
                setOnCompletionListener {
                    onCompletion.invoke()
                        // seekTo(0)
                }

            }
        }




    }

    override fun startPlayer() {
        mediaPlayer?.start()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
    }



    override fun releasePlayer() {
        mediaPlayer?.release()

    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
}