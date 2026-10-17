package com.example.musicplayerraaga

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MusicViewModel(private val context: Context) : ViewModel() {

    // Tracks the master list of songs available to the UI
    var songList = mutableStateListOf<Song>()

    // Tracks whichever song is currently selected or playing
    var currentSong by mutableStateOf<Song?>(null)

    // Tracks whether the audio engine is actively pushing sound
    var isPlaying by mutableStateOf(false)

    private var mediaPlayer: MediaPlayer? = null

    init {
        loadInitialSongs()
    }

    private fun loadInitialSongs() {
        // Level 1 & 2: Instantiates the hardcoded fallback list mapped to your newly pasted raw audio
        val localSongs = listOf(
            Song(
                id = 1,
                title = "Track One",
                artist = "Local Artist A",
                localImageRes = android.R.drawable.ic_menu_gallery,
                localAudioRes = context.resources.getIdentifier("song1", "raw", context.packageName)
            ),
            Song(
                id = 2,
                title = "Track Two",
                artist = "Local Artist B",
                localImageRes = android.R.drawable.ic_menu_gallery,
                localAudioRes = context.resources.getIdentifier("song2", "raw", context.packageName)
            ),
            Song(
                id = 3,
                title = "Track Three",
                artist = "Local Artist C",
                localImageRes = android.R.drawable.ic_menu_gallery,
                localAudioRes = context.resources.getIdentifier("song3", "raw", context.packageName)
            )
        )
        songList.addAll(localSongs)

        // Level 3: Attempts to safely load remote web playlists asynchronously
        fetchRemotePlaylist()
    }

    private fun fetchRemotePlaylist() {
        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://mockmusic.com") // Placeholder URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(MusicApiService::class.java)
                val remoteSongs = apiService.fetchSongs()
                if (remoteSongs.isNotEmpty()) {
                    songList.clear()
                    songList.addAll(remoteSongs)
                }
            } catch (_: Exception) {
                // If there's no internet or the API link is dummy, it fails silently
                // and safely leaves your hardcoded local songs active!
            }
        }
    }

    // Level 2: Controls target selection and safely plays music files
    fun onTrackSelected(song: Song) {
        if (currentSong?.id != song.id) {
            mediaPlayer?.release() // Clean up any old playing songs first
            currentSong = song

            // Checks if it should boot up the local .mp3 file or stream an online URL
            mediaPlayer = if (song.localAudioRes != null && song.localAudioRes != 0) {
                MediaPlayer.create(context, song.localAudioRes)
            } else if (!song.audioUrl.isNullOrEmpty()) {
                MediaPlayer().apply {
                    setDataSource(song.audioUrl)
                    prepare()
                }
            } else {
                null
            }

            mediaPlayer?.start()
            isPlaying = mediaPlayer != null
        }
    }

    // Level 1 & 2 UI Play/Pause button action handler
    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
            } else {
                it.start()
                isPlaying = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release() // Ensure audio stops completely if the app closes
        mediaPlayer = null
    }
}