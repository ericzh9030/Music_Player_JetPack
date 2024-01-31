package com.cs211d.musicplayerjetpack.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cs211d.musicplayerjetpack.MusicPlayerApplication
import com.cs211d.musicplayerjetpack.data.AppDataContainer
import com.cs211d.musicplayerjetpack.data.Player
import com.cs211d.musicplayerjetpack.data.Song
import com.cs211d.musicplayerjetpack.data.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val supportedFile = listOf("mp3", "mp4", "aac")

enum class SortedBy{
    TITLE, ALBUM, ARTIST
}

class AppViewModel (
    private val application: Application,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // initialize ViewModel with user preferences dependency
    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicPlayerApplication)
                AppViewModel(application, application.userPreferencesRepository)
            }
        }
    }

    val applicationContext: Context = application.applicationContext

    // media3 player
    val player = Player.getInstance(applicationContext)
    private var playImmediately = false

    // song database repository
    private val songRepository = AppDataContainer(application).songRepository

    // folder UI state
    val folderUiState: StateFlow<FolderUiState> = userPreferencesRepository.folderName.map {
        FolderUiState(currentFolder = it)
    } .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FolderUiState()
    )

    // song list UI state
    val songListUiState:StateFlow<SongListUiState> = userPreferencesRepository.songListUiPreferences
        .map {
            val sortedBy = SortedBy.valueOf(it[0])
            val selectedSongID = it[1]
            val selectedSong = if(selectedSongID != "") songRepository.getSongByID(selectedSongID.toLong()).first() else null
            SongListUiState(
                sortedBy=sortedBy,
                songList = getAllSongFromDB(sortedBy),
                selectedSong = selectedSong
            )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SongListUiState()
    )

    // player UI state
    val playerUiState: StateFlow<PlayerUiState> = userPreferencesRepository.songListUiPreferences
        .map {
            val selectedSongID = it[1]
            val selectedSong = if(selectedSongID != "") songRepository.getSongByID(selectedSongID.toLong()).first() else null
            if (selectedSong != null && !playImmediately){
                player.initializer(selectedSong.PATH.toUri(), playImmediately)
            }
            PlayerUiState(currentSong = selectedSong)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerUiState()
        )

    // save preferences of "sort by" options
    fun saveSortedByPreference(sortBy: SortedBy){
        viewModelScope.launch {
            userPreferencesRepository.saveSortedByPreference(sortBy)
        }
    }

    // save access permission for selected folder, for re-open App to access this folder
    private fun savePermission(uri: Uri){
        val contentResolver = application.contentResolver
        val takeFlags:Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
        contentResolver.takePersistableUriPermission(uri, takeFlags)
    }

    // recursively list all audio files from parent to children folders
    private suspend fun dfsListFiles(path: DocumentFile?):Boolean{

        path!!.listFiles().forEach {file ->
            if (file.isDirectory){
                // recursive
                dfsListFiles(file)
            }else{
                // if file extension is supported, add it to DB
                if (file.uri.toString().split(".").last() in supportedFile ){
                    // extract all metadata and put song into DB
                    val song = extractMetadata(file)
                    song.ID = songRepository.addSong(song)
                }
            }
        }
        return true
    }

    // clear database, list all audio files and put into DB, get sorted song list
    private suspend fun listAllFiles(uri: Uri){

        withContext(Dispatchers.IO){
            songRepository.clearSongDB()
            val path = DocumentFile.fromTreeUri(application.applicationContext, uri)
            if (dfsListFiles(path)){
                getAllSongFromDB(sortedBy = songListUiState.value.sortedBy)
            }
        }
    }

    // choose new folder to list all audio files underneath, save chosen folder to preferences
    suspend fun openFolder(uri: Uri){

        savePermission(uri)
        listAllFiles(uri)
        viewModelScope.launch {
            // stop and reset the player if currently playing
            player.reset()
            val folderName =uri.path.toString().split(":").last()
            userPreferencesRepository.saveSelectedFolderName(folderName)
        }
    }


    // when click song from song list screen, save chosen song to preferences, and play the song
    fun selectedSongFromList(song: Song){

        playImmediately = true
        viewModelScope.launch {
            userPreferencesRepository.saveSelectedSong(song)
            player.initializer(song.PATH.toUri(), playImmediately)
        }
    }

    // get sorted List<Song> from DB
    private suspend fun getAllSongFromDB(sortedBy: SortedBy = SortedBy.TITLE): List<Song> {

        return withContext(Dispatchers.IO){

            return@withContext when(sortedBy){
                SortedBy.ALBUM -> {
                    songRepository.getAllSongByAlbum().first()
                }
                SortedBy.ARTIST -> {
                    songRepository.getAllSongByArtist().first()
                }
                else -> {
                    songRepository.getAllSongByTitle().first()
                }
            }
        }
    }

    // extract metadata (title, artist, album, etc.) from audio file, then construct a Song object.
    private fun extractMetadata(file: DocumentFile): Song{

        val filePath = file.uri.toString()
        val filename = file.name.toString()
        val metaData = MediaMetadataRetriever()
        metaData.setDataSource(application, file.uri)

        // if no Title in audio file's metadata, use file name
        val title = metaData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: filename
        val album = metaData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: ""
        val artist = metaData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: ""

        return Song(ALBUM = album, TITLE = title, ARTIST = artist, FILENAME = filename, PATH = filePath)
    }
}

// UI state for folder screen
data class FolderUiState(val currentFolder: String = "N/A")

// UI state for Song list Screen
data class SongListUiState(
    val sortedBy: SortedBy = SortedBy.TITLE,
    val songList: List<Song> = listOf(),
    val selectedSong: Song? = null
)

// UI state for player screen
data class PlayerUiState(val currentSong: Song? = null)