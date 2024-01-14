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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.oAuthCredential
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RV_Events_adap(val context: Context): RecyclerView.Adapter<RV_Events_adap.ViewHolder>() {

    private var database = Firebase.firestore
    val currentuser = Firebase.auth.currentUser
    val userdata =  database.collection("user").document(currentuser?.uid.toString()).get()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = RvEventsRowsBinding.bind(itemView)
        val up_button = binding.upbutton
        val down_button = binding.downbutton
        var up_state = false
        var down_state = false
        val votes = binding.votes
        val heading = binding.heading
        val content = binding.content
        val view = itemView
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

//        val uid = Firebase.auth.uid
        val uid = "+918604643478"
        val eid = eventsdata[position].eventid
        database.collection("user").document(uid.toString()).get()
            .addOnSuccessListener {
                val data = it.data
                if(data?.get("event_liked") != null){
                    val event_liked = data["event_liked"] as Map<String,Boolean>
                    if(event_liked.containsKey(eid)){
                        if(event_liked[eid] == true){
                            holder.up_button.isEnabled = false
                            holder.up_button.setImageResource(R.drawable.arrow_up_green)
                            holder.down_button.isEnabled = true
                            holder.down_button.setImageResource(R.drawable.arrow_down_grey)
                        }
                        else{
                            holder.down_button.isEnabled = false
                            holder.down_button.setImageResource(R.drawable.arrow_down_red)
                            holder.up_button.isEnabled = true
                            holder.up_button.setImageResource(R.drawable.arrow_up_grey)
                        }
                    }
                }
            }

        holder.heading.text = eventsdata[position].Header
        holder.content.text = eventsdata[position].Dscrp
        val id = eventsdata[position].eventid
        var votes = eventsdata[position].votes
        holder.votes.text = votes.toString()

        holder.up_button.setOnClickListener(){
            holder.up_button.isEnabled = false;
            holder.up_button.setImageResource(R.drawable.arrow_up_green)
            holder.down_button.setImageResource(R.drawable.arrow_down_grey)

            if(holder.down_button.isEnabled==false){
//                holder.votes.text = votes.toInt().plus(2).toString()
                database.collection("Events").document(id).update("votes", FieldValue.increment(2))
                    .addOnSuccessListener{
                        votes+=2
                        holder.votes.text = votes.toString()
                        database.collection("user").document(uid.toString()).update("event_liked.$id",true)
                    }
            }
            else{
//                holder.votes.text = votes.toInt().plus(1).toString()
                database.collection("Events").document(id).update("votes", FieldValue.increment(1))
                    .addOnSuccessListener{
                        votes+=1
                        holder.votes.text = votes.toString()
                        database.collection("user").document(uid.toString()).update("event_liked.$id",true)
                    }
            }
            holder.down_button.isEnabled = true
        }

        holder.down_button.setOnClickListener(){

            holder.down_button.isEnabled = false
            holder.down_button.setImageResource(R.drawable.arrow_down_red)
            holder.up_button.setImageResource(R.drawable.arrow_up_grey)

            if(holder.up_button.isEnabled==false){
//                holder.votes.text = votes.toInt().minus(2).toString()
                database.collection("Events").document(id).update("votes", FieldValue.increment(-2))
                    .addOnSuccessListener{
                        votes-=2
                        holder.votes.text = votes.toString()
                        database.collection("user").document(uid.toString()).update("event_liked.$id",false)
                    }
            }
            else{
//                holder.votes.text = votes.toInt().minus(1).toString()
                database.collection("Events").document(id).update("votes", FieldValue.increment(-1))
                    .addOnSuccessListener{
                        votes-=1
                        holder.votes.text = votes.toString()
                        database.collection("user").document(uid.toString()).update("event_liked.$id",false)
                    }
            }

            holder.up_button.isEnabled = true
        }
    }
}