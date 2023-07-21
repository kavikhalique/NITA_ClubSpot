package com.example.nitaclubspot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.nitaclubspot.databinding.FragmentClubsAboutBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Clubs_About.newInstance] factory method to
 * create an instance of this fragment.
 */
class Clubs_About : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_clubs_about, container, false)

        val binding = FragmentClubsAboutBinding.bind(view)
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.loadUrl("https://dccnita.tech/")

        return view
    }
}