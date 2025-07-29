package com.calmly.meditation.service

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.calmly.meditation.MainActivity
import com.calmly.meditation.R
import com.calmly.meditation.model.SoundItem

class MediaPlaybackService : Service() {

    private val binder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var currentSound: SoundItem? = null
    private var isPlaying = false
    private var onPlaybackStateChanged: ((SoundItem?, Boolean) -> Unit)? = null

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "CALMLY_PLAYBACK"
        private const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        private const val ACTION_STOP = "ACTION_STOP"
    }

    inner class LocalBinder : Binder() {
        fun getService(): MediaPlaybackService = this@MediaPlaybackService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> togglePlayPause()
            ACTION_STOP -> stopSound()
        }
        return START_STICKY
    }

    fun setOnPlaybackStateChanged(callback: (SoundItem?, Boolean) -> Unit) {
        onPlaybackStateChanged = callback
    }

    fun playSound(soundItem: SoundItem) {
        stopCurrentSound()
        
        currentSound = soundItem
        mediaPlayer = MediaPlayer.create(this, soundItem.resourceId).apply {
            isLooping = true
            setOnPreparedListener {
                start()
                isPlaying = true
                updateNotification()
                notifyPlaybackStateChanged()
            }
            setOnErrorListener { _, _, _ ->
                stopSound()
                true
            }
        }
    }

    fun pauseSound() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
                updateNotification()
                notifyPlaybackStateChanged()
            }
        }
    }

    fun resumeSound() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                isPlaying = true
                updateNotification()
                notifyPlaybackStateChanged()
            }
        }
    }

    fun stopSound() {
        stopCurrentSound()
        currentSound = null
        isPlaying = false
        stopForeground(true)
        notifyPlaybackStateChanged()
    }

    private fun togglePlayPause() {
        if (isPlaying) {
            pauseSound()
        } else {
            resumeSound()
        }
    }

    private fun stopCurrentSound() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for meditation and sleep sounds"
                setSound(null, null)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification() {
        val sound = currentSound ?: return
        
        val playPauseIntent = Intent(this, MediaPlaybackService::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, 0, playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, MediaPlaybackService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mainIntent = Intent(this, MainActivity::class.java)
        val mainPendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Calmly")
            .setContentText(sound.title)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(mainPendingIntent)
            .addAction(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                if (isPlaying) "Pause" else "Play",
                playPausePendingIntent
            )
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1))
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun notifyPlaybackStateChanged() {
        onPlaybackStateChanged?.invoke(currentSound, isPlaying)
    }

    override fun onDestroy() {
        stopCurrentSound()
        super.onDestroy()
    }
}
