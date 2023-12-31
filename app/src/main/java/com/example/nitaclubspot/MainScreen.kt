package com.example.nitaclubspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.nitaclubspot.databinding.MainscreenBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class MainScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MainscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fm=supportFragmentManager
        val adapter = TV_Main_adapter(fm,lifecycle)
        adapter.add(Events())
        adapter.add(Clubs())
        binding.tabs.addTab(binding.tabs.newTab().setText("Events"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Clubs"))
        binding.viewPager.adapter=adapter

        binding.tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager.currentItem= tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabs.selectTab(binding.tabs.getTabAt(position))
            }
        })
    }

    fun login(view: View) {

//        val pref = getSharedPreferences("login", MODE_PRIVATE)
        val person= FirebaseAuth.getInstance().currentUser
        if(person!=null){
            //show profile
            intent = Intent(this,userProfile::class.java)
            startActivity(intent)
        }
        else{
            //show login
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

//    fun Clicked(view: View) {
//        Toast.makeText(this,"From Mainscreen",Toast.LENGTH_SHORT).show()
//    }
}

