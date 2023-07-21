package com.example.nitaclubspot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.nitaclubspot.databinding.ActivityClubDetailsBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.selects.select

class ClubDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityClubDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_club_details)

//        binding.pagerer.superp
        val adapter = TV_Main_adapter(supportFragmentManager,lifecycle)
        adapter.add(Clubs_About())
        adapter.add(Clubs_Events())
        binding.pager.adapter= adapter
        binding.tabs.addTab(binding.tabs.newTab().setText("About"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Events"))

        binding.tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.pager.currentItem=tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        binding.pager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabs.selectTab(binding.tabs.getTabAt(position))
            }
        })
    }

}