package com.calmly.meditation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.calmly.meditation.databinding.ActivityMainBinding
import com.calmly.meditation.fragment.MeditationFragment
import com.calmly.meditation.fragment.SleepFragment
import com.calmly.meditation.service.MediaPlaybackService
import com.calmly.meditation.viewmodel.SoundViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var soundViewModel: SoundViewModel
    private var mediaService: MediaPlaybackService? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlaybackService.LocalBinder
            mediaService = binder.getService()
            soundViewModel.setMediaService(mediaService!!)
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            mediaService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        soundViewModel = ViewModelProvider(this)[SoundViewModel::class.java]
        
        setupViewPager()
        bindMediaService()
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = ViewPagerAdapter(this)
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Meditation"
                1 -> "Sleep"
                else -> ""
            }
        }.attach()
    }

    private fun bindMediaService() {
        val intent = Intent(this, MediaPlaybackService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(serviceConnection)
        }
    }

    private class ViewPagerAdapter(fragmentActivity: FragmentActivity) : 
        FragmentStateAdapter(fragmentActivity) {
        
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MeditationFragment()
                1 -> SleepFragment()
                else -> MeditationFragment()
            }
        }
    }
}
