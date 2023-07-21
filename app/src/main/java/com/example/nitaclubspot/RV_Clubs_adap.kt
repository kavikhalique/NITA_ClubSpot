package com.example.nitaclubspot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.nitaclubspot.databinding.RvClubsGridBinding

class RV_Clubs_adap(val context:Context): RecyclerView.Adapter<RV_Clubs_adap.Viewholder>() {

    var clubsdata: ArrayList<ClubsData> = ArrayList()

    class Viewholder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding= RvClubsGridBinding.bind(itemView)
        val text= binding.text

        fun listener(cont: Context,data: ClubsData){
            itemView.setOnClickListener(){
                val intent = Intent(cont,ClubDetails::class.java)
                intent.putExtra("Data",data.Data)
                startActivity(cont,intent, Bundle())
            }
        }
    }

    fun add(text: String){
        clubsdata.add(ClubsData(text))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RV_Clubs_adap.Viewholder {
        return Viewholder(LayoutInflater.from(context).inflate(R.layout.rv_clubs_grid,parent,false))
    }

    override fun onBindViewHolder(holder: RV_Clubs_adap.Viewholder, position: Int) {
        holder.text.text=clubsdata[position].Data
        holder.listener(context,clubsdata[position])
    }

    override fun getItemCount(): Int {
        return clubsdata.size
    }
}