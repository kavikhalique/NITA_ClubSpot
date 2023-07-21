package com.example.nitaclubspot

import android.icu.lang.UCharacter.VerticalOrientation
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nitaclubspot.databinding.FragmentClubsBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Clubs.newInstance] factory method to
 * create an instance of this fragment.
 */
open class Clubs : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view=inflater.inflate(R.layout.fragment_clubs, container, false)
        val binding=FragmentClubsBinding.bind(view)

        val adapter= context?.let { RV_Clubs_adap(it) }
        for(i in 1..100){
            if (adapter != null) {
                adapter.add("Grid$i")
            }
        }

        binding.recyclerView.layoutManager= GridLayoutManager(context,2)
        binding.recyclerView.adapter=adapter

        return view
    }

}