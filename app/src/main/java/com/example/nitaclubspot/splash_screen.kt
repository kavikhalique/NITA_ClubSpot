package com.example.nitaclubspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import com.example.nitaclubspot.databinding.ActivitySplashScreenBinding
import com.example.nitaclubspot.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import java.util.logging.Handler
import java.util.logging.LogRecord

class splash_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.logo.setAnimation(AnimationUtils.loadAnimation(this,R.anim.top_to_bottom))
        binding.middle.setAnimation(AnimationUtils.loadAnimation(this,R.anim.middle_splash))

        android.os.Handler(Looper.getMainLooper()).postDelayed({
            val pref = getSharedPreferences("login", MODE_PRIVATE)
//            val isloggedin = pref.getBoolean("flag",false)

            val person= FirebaseAuth.getInstance().currentUser

            if(person!=null){
                intent = Intent(this,MainScreen::class.java)
                startActivity(intent)
            }
            else{
                intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 3000)
    }
}