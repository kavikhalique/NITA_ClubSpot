package com.example.nitaclubspot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.nitaclubspot.databinding.RvEventsRowsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RV_Events_adap(val context: Context): RecyclerView.Adapter<RV_Events_adap.ViewHolder>() {

    private var database = Firebase.firestore

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding= RvEventsRowsBinding.bind(itemView)
        val up_button = binding.upbutton
        val down_button = binding.downbutton
        var up_state=false
        var down_state=false

        val heading= binding.heading
        val content= binding.content
        val view= itemView
    }

    var eventsdata : ArrayList<EventsData> = ArrayList()

    fun add(Data: EventsData){
        eventsdata.add(Data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_events_rows,parent,false))
    }

    override fun getItemCount(): Int {
        return eventsdata.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.heading.text = eventsdata[position].Header
        holder.content.text = eventsdata[position].Dscrp

        holder.view.setOnClickListener(){
            val intent = Intent(context,EventDetails::class.java)
            intent.putExtra("Key",eventsdata[position].Header)
            intent.putExtra("Key2",eventsdata[position].Dscrp)
            startActivity(context,intent, Bundle())
        }

        holder.up_button.setOnClickListener(){
            // if button has already been clicked then disable it
            if(holder.up_state){
                holder.up_state=false
                holder.up_button.setImageResource(R.drawable.arrow_up_grey)
            }
            // if button was not clicked before then enable it
            else{
                holder.up_state=true
                holder.up_button.setImageResource(R.drawable.arrow_up_green)
            }
            // if another button is enabled then disable it
            if(holder.down_state){
                holder.down_button.setImageResource(R.drawable.arrow_down_grey)
                holder.down_state=false
            }
        }

        holder.down_button.setOnClickListener(){
            // if button has aready been clicked then disable it
            if(holder.down_state){
                holder.down_button.setImageResource(R.drawable.arrow_down_grey)
                holder.down_state=false
            }
            // if button was not clicked before then enable it
            else{
                holder.down_state=true
                holder.down_button.setImageResource(R.drawable.arrow_down_red)

            }
            // if another button is enabled then disable it
            if(holder.up_state){
                holder.up_state=false
                holder.up_button.setImageResource(R.drawable.arrow_up_grey)
            }
        }
    }
}