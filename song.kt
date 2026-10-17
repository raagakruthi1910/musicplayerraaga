package com.example.musicplayerraaga

import androidx.annotation.RawRes

data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val category: String = "Pop",
    val imageUrl: String? = null,
    val artworkUrl: String? = null,
    val localImageRes: Int? = null,
    val audioUrl: String? = null,
    @param:RawRes val localAudioRes: Int? = null,
    val durationMs: Long = 210000L
)

val sampleSongs = listOf(
    Song(1, "Blinding Lights", "The Weeknd", "Pop", imageUrl = "https://picsum.photos/id/1025/300/300", localAudioRes = R.raw.song1, durationMs = 200000L),
    Song(2, "Stay", "The Kid LAROI & Justin Bieber", "Pop", imageUrl = "https://picsum.photos/id/1062/300/300", localAudioRes = R.raw.song2, durationMs = 141000L),
    Song(3, "As It Was", "Harry Styles", "Indie", imageUrl = "https://picsum.photos/id/1069/300/300", localAudioRes = R.raw.song3, durationMs = 167000L),
    Song(4, "Shape of You", "Ed Sheeran", "Pop", imageUrl = "https://picsum.photos/id/1084/300/300", localAudioRes = R.raw.song1, durationMs = 233000L),
    Song(5, "Levitating", "Dua Lipa", "Dance", imageUrl = "https://picsum.photos/id/1080/300/300", localAudioRes = R.raw.song2, durationMs = 203000L)
)

val dynamicSongs = sampleSongs
