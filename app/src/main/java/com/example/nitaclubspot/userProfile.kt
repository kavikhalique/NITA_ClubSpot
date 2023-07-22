package com.example.nitaclubspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nitaclubspot.databinding.UserProfileBinding
import com.example.nitaclubspot.ui.login.LoginActivity

class userProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = UserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = getSharedPreferences("login", MODE_PRIVATE)
        val username=pref.getString("username",null)

        if(username!=null){
            binding.username.text=username
        }
        else binding.username.text="user"

        binding.logout.setOnClickListener(){
            val editor = pref.edit()
            editor.putBoolean("flag",false)
            editor.apply()

            finish()

            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}