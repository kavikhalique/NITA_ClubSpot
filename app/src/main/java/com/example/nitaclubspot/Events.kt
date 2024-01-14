package com.example.nitaclubspot

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nitaclubspot.databinding.FragmentEventsBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Events.newInstance] factory method to
 * create an instance of this fragment.
 */
class Events : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var database = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        // Inflate the layout for this fragment

        val view=inflater.inflate(R.layout.fragment_events, container, false)

        val adapter= context?.let { RV_Events_adap(it) }
        val temp: ArrayList<EventsData> = ArrayList()

        FragmentEventsBinding.bind(view).recyclerView.layoutManager= LinearLayoutManager(context)

        database.collection("Events").get()
            .addOnSuccessListener { result ->
                Log.d("TAG", "Success")
                for (document in result) {
                    adapter?.add(EventsData(document.id ,document.data
                        ["votes"].toString().toInt(),document.data["Heading"].toString(),document.data["Intro"].toString()))
                    FragmentEventsBinding.bind(view).recyclerView.adapter=adapter
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Events.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Events().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}