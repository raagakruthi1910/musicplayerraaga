package com.example.musicplayerraaga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = Color(0xFF0F0F12), modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Note: Music continues playing in background when app is minimized.
        // It is only stopped if the app process is explicitly cleared.
        if (isFinishing) {
            MusicPlayerController.stop()
        }
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    var currentTab by remember { mutableIntStateOf(0) }
    var showFullPlayer by remember { mutableStateOf(false) }

    val currentSong = MusicPlayerController.currentSong
    val isPlaying = MusicPlayerController.isPlaying

    val userName = "Music Lover"

    if (showFullPlayer && currentSong != null) {
        DisplayScreen(
            song = currentSong,
            isPlayingState = isPlaying,
            onTogglePlayback = { MusicPlayerController.togglePlayPause() },
            onNextClick = { MusicPlayerController.nextTrack(context) },
            onPreviousClick = { MusicPlayerController.previousTrack(context) },
            onBackClick = { showFullPlayer = false }
        )
    } else {
        Scaffold(
            containerColor = Color(0xFF0F0F12),
            bottomBar = {
                Column {
                    // Mini Player Bar when a track is active
                    if (currentSong != null) {
                        MiniPlayer(
                            song = currentSong,
                            isPlaying = isPlaying,
                            onExpand = { showFullPlayer = true }
                        )
                    }

                    // Bottom Navigation Bar
                    NavigationBar(
                        containerColor = Color(0xFF18181E),
                        contentColor = Color.White
                    ) {
                        NavigationBarItem(
                            selected = currentTab == 0,
                            onClick = { currentTab = 0 },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFFF4081),
                                selectedTextColor = Color(0xFFFF4081),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color(0xFF2C2C35)
                            )
                        )
                        NavigationBarItem(
                            selected = currentTab == 1,
                            onClick = { currentTab = 1 },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            label = { Text("Search") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFFF4081),
                                selectedTextColor = Color(0xFFFF4081),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color(0xFF2C2C35)
                            )
                        )
                        NavigationBarItem(
                            selected = currentTab == 2,
                            onClick = { currentTab = 2 },
                            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                            label = { Text("Favorites") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFFF4081),
                                selectedTextColor = Color(0xFFFF4081),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color(0xFF2C2C35)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    0 -> HomeScreen(
                        songs = sampleSongs,
                        userName = userName,
                        onSongClick = { song ->
                            MusicPlayerController.playSong(song, context)
                            showFullPlayer = true
                        }
                    )
                    1 -> SearchScreen(
                        onSongClick = { song ->
                            MusicPlayerController.playSong(song, context)
                            showFullPlayer = true
                        }
                    )
                    2 -> FavoritesScreen(
                        onSongClick = { song ->
                            MusicPlayerController.playSong(song, context)
                            showFullPlayer = true
                        }
                    )
                }
            }
        }
    }
}
