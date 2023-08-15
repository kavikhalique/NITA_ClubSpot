package com.example.nitaclubspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nitaclubspot.databinding.UserProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class userProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = UserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = getSharedPreferences("login", MODE_PRIVATE)
        val username=pref.getString("username",null)

        if(Firebase.auth.currentUser?.displayName!=null){
            binding.username.text= Firebase.auth.currentUser?.displayName
        }
        else binding.username.text="user"

        binding.logout.setOnClickListener(){
            val editor = pref.edit()
            editor.putBoolean("flag",false)
            editor.putString("username",null)
            editor.apply()

            //firebase logout
            Firebase.auth.signOut()

            //google logout
            GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
            finishAffinity()

            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

    }
}