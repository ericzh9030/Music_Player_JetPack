package com.cs211d.musicplayerjetpack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSong(song: Song): Long

    @Query("SELECT * FROM Song ORDER BY ALBUM IS '', ALBUM")
    fun getAllSongSortedByAlbum(): Flow<List<Song>>

    @Query("SELECT * FROM Song ORDER BY TITLE")
    fun getAllSongSortedByTitle(): Flow<List<Song>>

    @Query("SELECT * FROM Song ORDER BY ARTIST IS '', ARTIST")
    fun getAllSongSortedByArtist(): Flow<List<Song>>

    // get one song by its ID
    @Query("SELECT * FROM Song WHERE ID = :id")
    fun getSongByID(id: Long): Flow<Song?>

    @Query("DELETE FROM Song")
    suspend fun clearSongDB()

}