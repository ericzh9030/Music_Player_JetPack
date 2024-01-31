package com.cs211d.musicplayerjetpack.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.cs211d.musicplayerjetpack.ui.SortedBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


class UserPreferencesRepository (
    private val dataStore: DataStore<Preferences>
) {
    private companion object{
        val LIST_SORTED_BY = stringPreferencesKey("list_sorted_by")
        val FOLDER_NAME = stringPreferencesKey("folder_name")
        val SELECTED_SONG = stringPreferencesKey("selected_song")
        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveSelectedFolderName(folder: String){
        dataStore.edit { preferences ->
            preferences[FOLDER_NAME] = folder
        }
    }

    suspend fun saveSortedByPreference(sortedBy: SortedBy){
        dataStore.edit{preferences ->
            preferences[LIST_SORTED_BY] = sortedBy.name
        }
    }

    suspend fun saveSelectedSong(song: Song){
        dataStore.edit { preferences ->
            preferences[SELECTED_SONG] = song.ID.toString()
        }
    }

    val folderName: Flow<String> = dataStore.data
        .catch {
            if(it is IOException){
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            }else{
                throw it
            }
        }
        .map { preferences ->
            preferences[FOLDER_NAME] ?: "N/A"
        }

    val songListUiPreferences: Flow<List<String>> = dataStore.data
        .catch{
            if(it is IOException){
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            }else{
                throw it
            }
        }
        .map{ preferences ->
            listOf(
                preferences[LIST_SORTED_BY] ?: SortedBy.TITLE.name,
                preferences[SELECTED_SONG] ?: ""
            )
        }
}