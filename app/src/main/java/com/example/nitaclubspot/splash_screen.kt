package com.example.nitaclubspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nitaclubspot.databinding.ActivitySplashScreenBinding
import com.example.nitaclubspot.ui.login.LoginActivity
import java.util.logging.Handler
import java.util.logging.LogRecord

class splash_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashScreenBinding.inflate(layoutInflater)
//        setContentView(binding.root)


        binding.logo.alpha= 0f
        binding.logo.animate().setDuration(1500).alpha(1f).withEndAction{
            val pref = getSharedPreferences("login", MODE_PRIVATE)
            var isloggedin = pref.getBoolean("flag",false)

            if(isloggedin){
                intent = Intent(this,MainScreen::class.java)
                startActivity(intent)
            }
            else{
                intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}