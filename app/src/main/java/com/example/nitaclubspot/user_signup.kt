package com.example.nitaclubspot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.nitaclubspot.databinding.ActivityUserSignupBinding
import com.example.nitaclubspot.ui.login.afterTextChanged
import java.util.regex.Matcher
import java.util.regex.Pattern

class user_signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityUserSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.firstname.afterTextChanged {
            val firstname= binding.firstname.text.toString()
            if(!onlyalpha(firstname)){
                binding.firstname.error = "Only alphabets are allowed"
            }
        }

        binding.lastname.afterTextChanged {
            val lastname= binding.lastname.text.toString()
            if(!onlyalpha(lastname)){
                binding.lastname.error = "Only alphabets are allowed"
            }
        }

        binding.phone.afterTextChanged {
            val phone = binding.phone.text.toString()
            if(!onlynumeric(phone)){
                binding.phone.error = "Not valid number"
            }
        }

        binding.emailid.afterTextChanged {
            val emailid = binding.emailid.text.toString()
            if(!isemail(emailid)){
                binding.emailid.error="Not valid email"
            }
        }
    }

    private fun isemail(emailid: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(emailid).matches()
    }

    private fun onlynumeric(phone: String): Boolean {
        var ans=false
        if(phone.length==13 && phone[0]=='+'&&phone[1]=='9'&&phone[2]=='1'){
            ans=true
            for(i in 3..12){
                if(phone[i]<'0'||phone[i]>'9') ans=false
            }
        }
        else if(phone.length==11 && phone[0]=='0'){
            ans=true
            for(i in 1..10){
                if(phone[i]<'0'||phone[i]>'9') ans=false
            }
        }
        else if(phone.length==10){
            ans=true
            for(i in 0..9){
                if(phone[i]<'0'||phone[i]>'9') ans=false
            }
        }
        return ans
    }

    private fun onlyalpha(s: String): Boolean{
        return (s!=null)&& s.matches("^[a-zA-Z]*$".toRegex())
    }
}
