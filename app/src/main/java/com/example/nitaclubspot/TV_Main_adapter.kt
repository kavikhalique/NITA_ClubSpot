package com.example.nitaclubspot

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TV_Main_adapter(fm: FragmentManager, lc:Lifecycle) : FragmentStateAdapter(fm,lc) {

    var fraglist: ArrayList<Fragment> = ArrayList()

    fun add(frag: Fragment){
        fraglist.add(frag)
    }

    override fun getItemCount(): Int {
        return fraglist.size
    }

    override fun createFragment(position: Int): Fragment {
        return fraglist[position]
    }

}