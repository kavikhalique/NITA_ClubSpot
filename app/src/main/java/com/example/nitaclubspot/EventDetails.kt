package com.example.nitaclubspot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nitaclubspot.databinding.ActivityEventDetailsBinding

class EventDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.Heading.text=intent.getStringExtra("Key")
        binding.Content.text=intent.getStringExtra("Key2")
    }
}