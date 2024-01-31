package com.cs211d.musicplayerjetpack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Song (
    @PrimaryKey(autoGenerate = true)
    var ID: Long = 0,

    val ALBUM: String,

    val TITLE: String,

    val ARTIST: String,

    val FILENAME: String,

    val PATH: String
)