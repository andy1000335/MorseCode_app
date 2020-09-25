package com.example.morsecode

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer

class AudioPlayer(context: Context) {
    private val mediaPlayer = MediaPlayer()
    private val audio = context.resources.openRawResourceFd(R.raw.beep)
    private var status = false

    init {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setDataSource(audio.fileDescriptor, audio.startOffset, audio.length)
        audio.close()
        mediaPlayer.setVolume(1f, 1f)
    }

    fun converse() {
        status = if (status) {
            mediaPlayer.stop()
            false
        } else {
            mediaPlayer.prepare()
            mediaPlayer.start()
            true
        }
    }
}