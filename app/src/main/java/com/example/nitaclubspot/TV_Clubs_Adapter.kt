package com.example.nitaclubspot

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TV_Clubs_Adapter(fm:FragmentManager, lc:Lifecycle): FragmentStateAdapter(fm,lc) {

    var fragments:ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun add(frag: Fragment){
        fragments.add(frag)
    }
}