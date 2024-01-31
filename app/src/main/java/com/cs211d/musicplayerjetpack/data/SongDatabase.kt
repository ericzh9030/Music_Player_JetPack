package com.cs211d.musicplayerjetpack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Song::class], version = 1, exportSchema = false)
abstract class SongDatabase: RoomDatabase(){

    abstract fun songDao(): SongDao

    // singleton
    companion object {
        @Volatile
        private var Instance: SongDatabase? = null

        fun getDatabase(context: Context): SongDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, SongDatabase::class.java, name = "song_database")
                    .build().also { Instance = it }
            }
        }
    }

}