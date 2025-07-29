package com.calmly.meditation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.calmly.meditation.R
import com.calmly.meditation.databinding.ItemSoundBinding
import com.calmly.meditation.model.SoundItem

class SoundAdapter(
    private val onSoundClick: (SoundItem) -> Unit
) : ListAdapter<SoundItem, SoundAdapter.SoundViewHolder>(SoundDiffCallback()) {

    private var currentPlayingSound: SoundItem? = null
    private var isPlaying: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val binding = ItemSoundBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SoundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateCurrentPlaying(soundItem: SoundItem?) {
        currentPlayingSound = soundItem
        notifyDataSetChanged()
    }

    fun updatePlayingState(playing: Boolean) {
        isPlaying = playing
        notifyDataSetChanged()
    }

    inner class SoundViewHolder(
        private val binding: ItemSoundBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(soundItem: SoundItem) {
            binding.apply {
                titleText.text = soundItem.title
                descriptionText.text = soundItem.description
                thumbnailImage.setImageResource(soundItem.thumbnailId)
                
                val isCurrentSound = currentPlayingSound?.id == soundItem.id
                val shouldShowPause = isCurrentSound && isPlaying
                
                playButton.setImageResource(
                    if (shouldShowPause) R.drawable.ic_pause else R.drawable.ic_play
                )
                
                // Visual feedback for currently playing sound
                root.alpha = if (isCurrentSound) 0.8f else 1.0f
                
                playButton.setOnClickListener {
                    onSoundClick(soundItem)
                }
                
                root.setOnClickListener {
                    onSoundClick(soundItem)
                }
            }
        }
    }

    private class SoundDiffCallback : DiffUtil.ItemCallback<SoundItem>() {
        override fun areItemsTheSame(oldItem: SoundItem, newItem: SoundItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SoundItem, newItem: SoundItem): Boolean {
            return oldItem == newItem
        }
    }
}
