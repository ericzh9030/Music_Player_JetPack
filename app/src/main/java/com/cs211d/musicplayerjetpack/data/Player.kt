package com.cs211d.musicplayerjetpack.data

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

// media3 player
class Player (context: Context) {

    // singleton
    companion object{
        private var instance: Player? = null

        fun getInstance(context: Context): Player{
            if (instance == null){
                instance = Player(context)
            }
            return instance!!
        }
    }

    private val player = ExoPlayer.Builder(context).build()

    fun getPlayer(): ExoPlayer {
        return player
    }

    fun initializer(uri: Uri, playImmediately: Boolean){
        val mediaItem = MediaItem.fromUri(uri)
        player.apply {
            stop()
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = playImmediately
        }
    }

    fun reset(){
        player.apply {
            stop()
            clearMediaItems()
        }
    }

}