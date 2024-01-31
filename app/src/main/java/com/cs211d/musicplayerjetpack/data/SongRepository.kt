package com.cs211d.musicplayerjetpack.data

import kotlinx.coroutines.flow.Flow

interface SongRepository{

    suspend fun addSong(song:Song): Long

    fun getAllSongByAlbum(): Flow<List<Song>>

    fun getAllSongByTitle(): Flow<List<Song>>

    fun getAllSongByArtist(): Flow<List<Song>>

    fun getSongByID(id: Long): Flow<Song?>

    suspend fun clearSongDB()
}
