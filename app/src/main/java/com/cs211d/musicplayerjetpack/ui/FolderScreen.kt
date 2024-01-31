package com.cs211d.musicplayerjetpack.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs211d.musicplayerjetpack.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FolderScreen(
    appViewModel: AppViewModel,
    onClickPlayer:() -> Unit,
    onClickList:() ->Unit,
    onClickFolder:() -> Unit
){
    val uiState by appViewModel.folderUiState.collectAsState()

    Column {

        Card (shape = CutCornerShape(0), modifier = Modifier.weight(1f)) {
            FolderSelection(uiState = uiState, appViewModel = appViewModel)
        }

        Card (shape = CutCornerShape(0)) {
            FolderNavigationButton(onClickPlayer = onClickPlayer, onClickList = onClickList, onClickFolder = onClickFolder)
        }
    }
}

// display folder icon and selected path
@Composable
fun FolderSelection(uiState: FolderUiState, appViewModel: AppViewModel){
    Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        OpenFolderButton(appViewModel = appViewModel)
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = uiState.currentFolder,
            fontSize = 32.sp,
            fontStyle = FontStyle.Italic,
            lineHeight = 40.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

// folder icon button, to launch select folder Intent
@Composable
fun OpenFolderButton(
    appViewModel: AppViewModel
){
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)

    val openFolder = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK){
                CoroutineScope (Dispatchers.IO).launch {
                    appViewModel.openFolder(uri = Uri.parse(it.data?.dataString))
                }
            }
        }
    )

    Row (verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Choose a Folder", fontSize = 24.sp, modifier = Modifier.padding(8.dp))
        IconButton(
            modifier = Modifier.size(72.dp),
            onClick = {
                openFolder.launch(intent)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_create_new_folder_128),
                contentDescription = "Selected Music Folder"
            )
        }
    }

}

// bottom navigation buttons
@Composable
fun FolderNavigationButton(
    onClickPlayer:() -> Unit,
    onClickList:() ->Unit,
    onClickFolder:() -> Unit
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
                .padding(8.dp)
        ) {
            Text(text = "List")
        }
        Button(
            onClick = onClickFolder,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            enabled = false
        ) {
            Text(text = "Folder")
        }
    }
}
