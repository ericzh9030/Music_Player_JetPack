package com.cs211d.musicplayerjetpack.data

import android.content.Context


interface AppContainer {
    val songRepository: SongRepository
}

class AppDataContainer(private val context: Context) :AppContainer {
    override val songRepository: SongRepository by lazy {
        OfflineSongRepository(SongDatabase.getDatabase(context).songDao())
    }
}