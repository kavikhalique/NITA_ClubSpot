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
class RV_Events_adap(val context: Context): RecyclerView.Adapter<RV_Events_adap.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding= RvEventsRowsBinding.bind(itemView)

        val heading= binding.heading
        val content= binding.content
        val view= itemView
    }

    var eventsdata : ArrayList<EventsData> = ArrayList()

    fun add(heading:String,content:String){
        eventsdata.add(EventsData(heading,content))
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
    }
}