package com.example.nitaclubspot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import com.example.nitaclubspot.databinding.ActivityUserSignupBinding
import com.example.nitaclubspot.ui.login.afterTextChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.concurrent.thread

class user_signup : AppCompatActivity() {
    var job: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityUserSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //animation

//        CoroutineScope(Dispatchers.Main).launch{
//            val print="Hello!!! Kavi Khalique"
//            var toput=""
//            for(i in print){
//                delay(100)
//                toput+=i
//                binding.name.text=toput
//            }
//        }

        //animation

//        binding.name.setAnimation(AnimationUtils.loadAnimation(this,R.anim.top_to_bottom))

//        printfn(binding.name)

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
        //function to check if phone number is valid or not
        binding.phone.afterTextChanged {



            val phone = binding.phone.text.toString()
            if(!onlynumeric(phone)){
                binding.phone.error = "Not valid number"
            }
        }
        //
        binding.emailid.afterTextChanged {
            val emailid = binding.emailid.text.toString()
            if(!isemail(emailid)){
                binding.emailid.error="Not valid email"
            }
        }
    }

//    private fun printfn(id: TextView) {
//        Thread() {
//            val print="Hello!!! Kavi Khalique"
//            var toput=""
//            for(i in print){
//                Thread.sleep(100)
//                toput+=i
//                runOnUiThread {
//                    id.text=toput
//                }
//            }
//        }.start()
//    }

    // Function to check if email is valid or not
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

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                job= CoroutineScope(Dispatchers.Main).launch{
                    delay(1000)
                    afterTextChanged.invoke(editable.toString())
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                job?.cancel()
            }
        })
    }

}
