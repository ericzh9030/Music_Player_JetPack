package com.cs211d.musicplayerjetpack.ui

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.LegacyPlayerControlView
import com.cs211d.musicplayerjetpack.data.Song
import com.cs211d.musicplayerjetpack.ui.theme.Typography


@OptIn(UnstableApi::class) @Composable
fun PlayerScreen(
    appViewModel: AppViewModel,
    onClickPlayer:()->Unit,
    onClickList:()->Unit,
    onClickFolder:()->Unit
){
    Column (horizontalAlignment = Alignment.CenterHorizontally) {

        val uiState by appViewModel.playerUiState.collectAsState()

        // metadata
        Card (shape = CutCornerShape(0), modifier = Modifier.weight(1f)) {
            SongMetadata(song = uiState.currentSong)
        }

        // playback controller
        AndroidView(factory = {
            LegacyPlayerControlView(appViewModel.applicationContext).apply{
                player = appViewModel.player.getPlayer()
                showTimeoutMs = 0
            }
        })

        // navigation buttons
        Card (shape = CutCornerShape(0)) {
            PlayerScreenNavigationButton(onClickPlayer = onClickPlayer, onClickList = onClickList, onClickFolder = onClickFolder)
        }
    }
}



// display current song metadata
@Composable
fun SongMetadata(song: Song?){

    val title = song?.TITLE ?: ""
    val artist = if(song != null && song.ARTIST != "") "by ${song.ARTIST}" else ""
    val album = if(song != null && song.ALBUM != "") "Album - ${song.ALBUM}" else ""

    Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        Text(
            text = title,
            style = Typography.headlineLarge
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = artist,
            style = Typography.headlineMedium
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Text(
            text = album,
            style = Typography.headlineSmall
        )
    }
}

// bottom navigation buttons
@Composable
fun PlayerScreenNavigationButton(
    onClickPlayer:()->Unit,
    onClickList:()->Unit,
    onClickFolder:()->Unit
){
    Row {
        Button(
            onClick = onClickPlayer,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            enabled = false
        ) {
            Text(text = "Player")
        }
        Button(
            onClick = onClickList,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(text = "List")
        }
        Button(
            onClick = onClickFolder,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(text = "Folder")
        }
    }
}
