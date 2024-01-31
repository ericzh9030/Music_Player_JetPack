package com.cs211d.musicplayerjetpack.data

import kotlinx.coroutines.flow.Flow

class OfflineSongRepository (private val songDao: SongDao) : SongRepository {

    override suspend fun addSong(song: Song): Long {
        song.ID = songDao.addSong(song)
        return song.ID
    }

    override suspend fun clearSongDB() {
        songDao.clearSongDB()
    }

    override fun getAllSongByAlbum(): Flow<List<Song>> {
        return songDao.getAllSongSortedByAlbum()
    }

    override fun getAllSongByTitle(): Flow<List<Song>> {
        return songDao.getAllSongSortedByTitle()
    }

    override fun getAllSongByArtist(): Flow<List<Song>> {
        return songDao.getAllSongSortedByArtist()
    }

    override fun getSongByID(id: Long): Flow<Song?> {
        return songDao.getSongByID(id)
    }
}