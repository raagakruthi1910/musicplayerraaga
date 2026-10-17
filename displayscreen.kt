package com.example.musicplayerraaga

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayScreen(
    song: Song,
    isPlayingState: Boolean,
    onTogglePlayback: (Boolean) -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val isFav = MusicPlayerController.favoriteSongIds.contains(song.id)

    // Position timer updater loop
    LaunchedEffect(isPlayingState) {
        while (isPlayingState) {
            MusicPlayerController.updatePosition()
            delay(500)
        }
    }

    // Vinyl Rotation Animation when playing
    val infiniteTransition = rememberInfiniteTransition(label = "VinylRotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Angle"
    )

    Scaffold(
        containerColor = Color(0xFF0F0F12),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        "Now Playing",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { MusicPlayerController.toggleFavorite(song.id) }) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFav) Color(0xFFFF4081) else Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Vinyl Record Art Frame
            val artUrl = song.artworkUrl ?: song.imageUrl
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .rotate(if (isPlayingState) rotationAngle else 0f)
                    .background(
                        Brush.sweepGradient(
                            colors = listOf(Color(0xFFFF4081), Color(0xFF651FFF), Color(0xFFFF4081))
                        )
                    )
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!artUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = artUrl,
                        contentDescription = "Artwork",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else if (song.localImageRes != null && song.localImageRes != 0) {
                    Image(
                        painter = painterResource(id = song.localImageRes),
                        contentDescription = "Artwork",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF1E1E24)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Visual",
                            tint = Color(0xFFFF4081),
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Song Title & Artist
            Text(
                text = song.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = song.artist,
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Seekbar Slider & Timers
            val posMs = MusicPlayerController.currentPositionMs
            val durMs = if (MusicPlayerController.durationMs > 0) MusicPlayerController.durationMs else song.durationMs
            var sliderPos by remember { mutableFloatStateOf(0f) }
            val currentPos = if (posMs > 0) posMs.toFloat() else sliderPos

            Slider(
                value = currentPos.coerceIn(0f, durMs.toFloat()),
                onValueChange = { newValue ->
                    sliderPos = newValue
                    MusicPlayerController.seekTo(newValue.toLong())
                },
                valueRange = 0f..durMs.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFFF4081),
                    activeTrackColor = Color(0xFFFF4081),
                    inactiveTrackColor = Color(0xFF2C2C35)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMs(posMs),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = formatMs(durMs),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Shuffle Button
                IconButton(onClick = { MusicPlayerController.isShuffle = !MusicPlayerController.isShuffle }) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (MusicPlayerController.isShuffle) Color(0xFFFF4081) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous Button
                IconButton(onClick = { onPreviousClick() }) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Core Play/Pause Button
                Button(
                    onClick = { onTogglePlayback(!isPlayingState) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = CircleShape,
                    modifier = Modifier.size(72.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (isPlayingState) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Playback Control",
                        tint = Color(0xFF0F0F12),
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Next Button
                IconButton(onClick = { onNextClick() }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Repeat Button
                IconButton(onClick = { MusicPlayerController.isRepeat = !MusicPlayerController.isRepeat }) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (MusicPlayerController.isRepeat) Color(0xFFFF4081) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSeconds = (ms / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
