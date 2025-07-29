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
import com.calmly.meditation.databinding.FragmentSleepBinding
import com.calmly.meditation.model.SoundCategory
import com.calmly.meditation.model.SoundItem
import com.calmly.meditation.viewmodel.SoundViewModel

class SleepFragment : Fragment() {
    
    private var _binding: FragmentSleepBinding? = null
    private val binding get() = _binding!!
    
    private val soundViewModel: SoundViewModel by activityViewModels()
    private lateinit var soundAdapter: SoundAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSleepBinding.inflate(inflater, container, false)
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
        
        soundAdapter.submitList(getSleepSounds())
    }

    private fun observeViewModel() {
        soundViewModel.currentPlayingSound.observe(viewLifecycleOwner) { currentSound ->
            soundAdapter.updateCurrentPlaying(currentSound)
        }
        
        soundViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            soundAdapter.updatePlayingState(isPlaying)
        }
    }

    private fun getSleepSounds(): List<SoundItem> {
        return listOf(
            SoundItem("sleep_whitenoise", "White Noise", "Pure white noise", 
                R.raw.white_noise, R.drawable.whitenoise_thumb, SoundCategory.SLEEP),
            SoundItem("sleep_fan", "Fan Sound", "Gentle fan noise", 
                R.raw.fan_sounds, R.drawable.fan_thumb, SoundCategory.SLEEP),
            SoundItem("sleep_lullaby", "Soft Lullaby", "Peaceful lullaby", 
                R.raw.lullaby_sounds, R.drawable.lullaby_thumb, SoundCategory.SLEEP),
            SoundItem("sleep_hum", "Deep Hum", "Low frequency hum", 
                R.raw.deep_hum, R.drawable.hum_thumb, SoundCategory.SLEEP),
            SoundItem("sleep_nature", "Night Nature", "Crickets and night sounds", 
                R.raw.night_nature, R.drawable.night_thumb, SoundCategory.SLEEP)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
