package com.example.musicplayerraaga

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.*

object MusicPlayerController {
    var currentSong by mutableStateOf<Song?>(null)
    var isPlaying by mutableStateOf(false)
    val favoriteSongIds = mutableStateSetOf<Int>(1, 3)
    var isShuffle by mutableStateOf(false)
    var isRepeat by mutableStateOf(false)
    var currentPositionMs by mutableLongStateOf(0L)
    var durationMs by mutableLongStateOf(0L)

    private var mediaPlayer: MediaPlayer? = null

    fun playSong(song: Song, context: Context) {
        if (currentSong?.id == song.id && mediaPlayer != null) {
            togglePlayPause()
            return
        }

        stop()
        currentSong = song

        val audioRes = song.localAudioRes
        if (audioRes != null && audioRes != 0) {
            try {
                mediaPlayer = MediaPlayer.create(context.applicationContext, audioRes)?.apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    isLooping = isRepeat
                    start()
                    this@MusicPlayerController.durationMs = duration.toLong()
                    this@MusicPlayerController.isPlaying = true

                    setOnCompletionListener {
                        if (isRepeat) {
                            start()
                        } else {
                            nextTrack(context)
                        }
                    }
                }
            } catch (_: Exception) {
                isPlaying = false
            }
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                isPlaying = false
            } else {
                player.start()
                isPlaying = true
            }
        }
    }

    fun nextTrack(context: Context) {
        val current = currentSong ?: return
        val list = sampleSongs
        if (list.isEmpty()) return

        val nextIndex = if (isShuffle) {
            (list.indices).random()
        } else {
            (list.indexOf(current) + 1) % list.size
        }
        playSong(list[nextIndex], context)
    }

    fun previousTrack(context: Context) {
        val current = currentSong ?: return
        val list = sampleSongs
        if (list.isEmpty()) return

        val currentIndex = list.indexOf(current)
        val prevIndex = if (currentIndex <= 0) list.size - 1 else currentIndex - 1
        playSong(list[prevIndex], context)
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.seekTo(positionMs.toInt())
        currentPositionMs = positionMs
    }

    fun toggleFavorite(songId: Int) {
        if (favoriteSongIds.contains(songId)) {
            favoriteSongIds.remove(songId)
        } else {
            favoriteSongIds.add(songId)
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        isPlaying = false
    }

    fun updatePosition() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                currentPositionMs = player.currentPosition.toLong()
                durationMs = player.duration.toLong()
            }
        }
    }
}
