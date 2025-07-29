package com.calmly.meditation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.calmly.meditation.R
import com.calmly.meditation.adapter.SoundAdapter
import com.calmly.meditation.databinding.FragmentMeditationBinding
import com.calmly.meditation.model.SoundCategory
import com.calmly.meditation.model.SoundItem
import com.calmly.meditation.viewmodel.SoundViewModel

class MeditationFragment : Fragment() {
    
    private var _binding: FragmentMeditationBinding? = null
    private val binding get() = _binding!!
    
    private val soundViewModel: SoundViewModel by activityViewModels()
    private lateinit var soundAdapter: SoundAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeditationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        soundAdapter = SoundAdapter { soundItem ->
            soundViewModel.togglePlayPause(soundItem)
        }
        
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = soundAdapter
        }
        
        soundAdapter.submitList(getMeditationSounds())
    }

    private fun observeViewModel() {
        soundViewModel.currentPlayingSound.observe(viewLifecycleOwner) { currentSound ->
            soundAdapter.updateCurrentPlaying(currentSound)
        }
        
        soundViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            soundAdapter.updatePlayingState(isPlaying)
        }
    }

    private fun getMeditationSounds(): List<SoundItem> {
        return listOf(
            SoundItem("med_forest", "Forest Ambience", "Peaceful forest sounds", 
                R.raw.forest_sounds, R.drawable.forest_thumb, SoundCategory.MEDITATION),
            SoundItem("med_rain", "Gentle Rain", "Soft rainfall", 
                R.raw.rain_sounds, R.drawable.rain_thumb, SoundCategory.MEDITATION),
            SoundItem("med_ocean", "Ocean Waves", "Calming ocean waves", 
                R.raw.ocean_sounds, R.drawable.ocean_thumb, SoundCategory.MEDITATION),
            SoundItem("med_campfire", "Campfire", "Crackling campfire", 
                R.raw.campfire_sounds, R.drawable.campfire_thumb, SoundCategory.MEDITATION),
            SoundItem("med_birds", "Morning Birds", "Peaceful bird songs", 
                R.raw.birds_sounds, R.drawable.birds_thumb, SoundCategory.MEDITATION)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
