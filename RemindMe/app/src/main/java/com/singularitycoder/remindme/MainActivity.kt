package com.singularitycoder.remindme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.singularitycoder.remindme.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewPager()
    }

    private fun setUpViewPager() {
        binding.viewpagerReminders.adapter = RemindersViewPagerAdapter(fragmentManager = supportFragmentManager, lifecycle = lifecycle)
        TabLayoutMediator(binding.tabLayoutReminders, binding.viewpagerReminders) { tab, position ->
            tab.text = remindersTabNamesList[position]
        }.attach()
    }

    inner class RemindersViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int = remindersTabNamesList.size
        override fun createFragment(position: Int): Fragment = RemindersFragment.newInstance(remindersTabNamesList[position])
    }
}