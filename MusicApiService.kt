package com.example.musicplayerraaga

import retrofit2.http.GET

interface MusicApiService {
    @GET("music/playlist.json")
    suspend fun fetchSongs(): List<Song>
}
