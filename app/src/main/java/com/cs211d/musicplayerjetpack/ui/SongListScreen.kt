package com.cs211d.musicplayerjetpack.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs211d.musicplayerjetpack.data.Song
import com.cs211d.musicplayerjetpack.ui.theme.Typography


@Composable
fun SongListScreen(
    appViewModel: AppViewModel,
    onClickPlayer:() -> Unit,
    onClickList:() ->Unit,
    onClickFolder:() -> Unit
){

    val uiState by appViewModel.songListUiState.collectAsState()

    Column (horizontalAlignment = Alignment.CenterHorizontally) {

        // radiobutton options
        Card (shape = CutCornerShape(0), modifier = Modifier.fillMaxWidth()) {
            SortRadioOption(uiState = uiState, appViewModel = appViewModel)
        }

        // song list
        Card (modifier = Modifier.weight(1f), shape = CutCornerShape(0)) {
            SongList(uiState = uiState, appViewModel = appViewModel)
        }

        // navigation buttons
        Card (shape = CutCornerShape(0)) {
            SongListNavigationButton(onClickPlayer = onClickPlayer, onClickList = onClickList, onClickFolder = onClickFolder)
        }
    }
}

// "sort by" radiobutton options
@Composable
fun SortRadioOption(uiState: SongListUiState, appViewModel: AppViewModel){

    val radioOptions = SortedBy.entries.toList()
    Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        Text(text = "Sort by: ")
        radioOptions.forEach { sortedBy ->
            Row (verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = sortedBy == uiState.sortedBy,
                    onClick = { appViewModel.saveSortedByPreference(sortedBy) }
                )
                Text(text = sortedBy.name)
            }
        }
    }

}



// scrollable list of song items
@Composable
fun SongList(uiState: SongListUiState, appViewModel: AppViewModel){
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)){
        items(uiState.songList){song ->
            SongItem(
                song = song,
                uiState = uiState,
                appViewModel = appViewModel
            )
        }
    }
}

// single song item on the list
@Composable
fun SongItem(song: Song, uiState: SongListUiState, appViewModel: AppViewModel){

    var fontWeight = FontWeight.Normal
    var offSet = Icons.Default.PlayArrow.defaultWidth

    Row (verticalAlignment = Alignment.CenterVertically){
        if (song == uiState.selectedSong) {
            Icon(Icons.Default.PlayArrow, contentDescription = "current song")
            fontWeight = FontWeight.Bold
            offSet = 0.dp
        }

        Text(
            text = song.TITLE,
            style = Typography.headlineLarge,
            fontWeight = fontWeight,
            modifier = Modifier
                .clickable { appViewModel.selectedSongFromList(song) }
                .offset(offSet)
        )
    }
}

// bottom navigation buttons
@Composable
fun SongListNavigationButton(
    onClickPlayer:()->Unit,
    onClickList:()->Unit,
    onClickFolder:()->Unit
){
    Row {
        Button(
            onClick = onClickPlayer,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(text = "Player")
        }
        Button(
            onClick = onClickList,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            enabled = false
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

