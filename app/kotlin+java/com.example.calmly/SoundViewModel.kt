package com.calmly.meditation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calmly.meditation.model.SoundItem
import com.calmly.meditation.service.MediaPlaybackService

class SoundViewModel : ViewModel() {
    
    private var mediaService: MediaPlaybackService? = null
    
    private val _currentPlayingSound = MutableLiveData<SoundItem?>()
    val currentPlayingSound: LiveData<SoundItem?> = _currentPlayingSound
    
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    fun setMediaService(service: MediaPlaybackService) {
        mediaService = service
        service.setOnPlaybackStateChanged { soundItem, playing ->
            _currentPlayingSound.value = soundItem
            _isPlaying.value = playing
        }
    }

    fun playSound(soundItem: SoundItem) {
        mediaService?.playSound(soundItem)
    }

    fun pauseSound() {
        mediaService?.pauseSound()
    }

    fun stopSound() {
        mediaService?.stopSound()
    }

    fun togglePlayPause(soundItem: SoundItem) {
        val currentSound = _currentPlayingSound.value
        val playing = _isPlaying.value ?: false
        
        when {
            currentSound?.id == soundItem.id && playing -> pauseSound()
            currentSound?.id == soundItem.id && !playing -> mediaService?.resumeSound()
            else -> playSound(soundItem)
        }
    }
}

// SoundItem.kt
package com.calmly.meditation.model

data class SoundItem(
    val id: String,
    val title: String,
    val description: String,
    val resourceId: Int,
    val thumbnailId: Int,
    val category: SoundCategory
)

enum class SoundCategory {
    MEDITATION, SLEEP
}
