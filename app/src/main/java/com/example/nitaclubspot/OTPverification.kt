package com.example.nitaclubspot

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.widget.addTextChangedListener
import com.example.nitaclubspot.databinding.ActivityOtpverificationBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class OTPverification : AppCompatActivity() {

    val auth= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //otp input
        var otp: String = ""

        binding.otp1.afterTextChanged {
            if(binding.otp1.text.toString().length==1){
                binding.otp2.requestFocus()
                otp+=binding.otp1.text.toString()
            }

        }
        binding.otp2.afterTextChanged {
            if(binding.otp2.text.toString().length==1){
                binding.otp3.requestFocus()
                otp+=binding.otp2.text.toString()
            }

        }
        binding.otp3.afterTextChanged {
            if(binding.otp3.text.toString().length==1){
                binding.otp4.requestFocus()
                otp+=binding.otp3.text.toString()
            }

        }
        binding.otp4.afterTextChanged {
            if(binding.otp4.text.toString().length==1){
                binding.otp5.requestFocus()
                otp+=binding.otp4.text.toString()
            }

        }
        binding.otp5.afterTextChanged {
            if(binding.otp5.text.toString().length==1){
                binding.otp6.requestFocus()
                otp+=binding.otp5.text.toString()
            }

        }
        binding.otp6.afterTextChanged {
            if(binding.otp6.text.toString().length==1){
                otp+=binding.otp6.text.toString()
            }

        }

        binding.otp1.setOnKeyListener(View.OnKeyListener(){ v, keyCode, event ->
            if(keyCode==KeyEvent.KEYCODE_DEL){
                binding.otp1.requestFocus()
                otp=""
            }
            false
        })
        binding.otp2.setOnKeyListener(View.OnKeyListener(){ v, keyCode, event ->
            if(keyCode==KeyEvent.KEYCODE_DEL && binding.otp2.text.toString().length==0){
                binding.otp1.requestFocus()
                otp=otp.substring(0,otp.length-1)
            }
            false
        })
        binding.otp3.setOnKeyListener(View.OnKeyListener(){ v, keyCode, event ->
            if(keyCode==KeyEvent.KEYCODE_DEL && binding.otp3.text.toString().length==0){
                binding.otp2.requestFocus()
                otp=otp.substring(0,otp.length-1)
            }
            false
        })
        binding.otp4.setOnKeyListener(View.OnKeyListener(){ v, keyCode, event ->
            if(keyCode==KeyEvent.KEYCODE_DEL && binding.otp4.text.toString().length==0){
                binding.otp3.requestFocus()
                otp=otp.substring(0,otp.length-1)
            }
            false
        })
        binding.otp5.setOnKeyListener(View.OnKeyListener(){ v, keyCode, event ->
            if(keyCode==KeyEvent.KEYCODE_DEL && binding.otp5.text.toString().length==0){
                binding.otp4.requestFocus()
                otp=otp.substring(0,otp.length-1)
            }
            false
        })
        binding.otp6.setOnKeyListener(View.OnKeyListener(){ v, keyCode, event ->
            if(keyCode==KeyEvent.KEYCODE_DEL && binding.otp6.text.toString().length==0){
                binding.otp5.requestFocus()
                otp=otp.substring(0,otp.length-1)
            }
            else if(keyCode==KeyEvent.KEYCODE_ENTER){
                binding.verify.performClick()
            }
            false
        })






        //otp verification


        var storedVerificationId:String?=null
        var resendToken: PhoneAuthProvider.ForceResendingToken?=null
        val phoneNumber= intent.getStringExtra("phonenumber")
        val firstname= intent.getStringExtra("firstname")
        val lastname= intent.getStringExtra("lastname")
        val email= intent.getStringExtra("email")
        val username = intent.getStringExtra("username")
        val registered = intent.getBooleanExtra("registered",true)

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(ContentValues.TAG, "onVerificationCompleted:$credential")

                signInWithPhoneAuthCredential(credential,registered,firstname,lastname,email,phoneNumber!!,username)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(ContentValues.TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(this@OTPverification,"Invalid Phone Number", Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(this@OTPverification,"Quota Exceeded", Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                    Toast.makeText(this@OTPverification,"Null Activity", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this@OTPverification,"Error Occured", Toast.LENGTH_SHORT).show()
                }

                // Show a message and update the UI

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(ContentValues.TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }

        Log.d("TAG", "phone number "+phoneNumber.toString())

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber.toString()) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        Log.d("TAG", "called for verification automatically")
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d("TAG", "storedVerificationId "+storedVerificationId.toString())

        binding.verify.setOnClickListener {
            Log.d("TAG", otp.toString())
//            Toast.makeText(this@OTPverification,otp,Toast.LENGTH_SHORT).show()
            Log.d("TAG", storedVerificationId.toString())

            if(otp.length==6){
                val credential = PhoneAuthProvider.getCredential(storedVerificationId.toString(), otp)
                Log.d("TAG", credential.smsCode.toString())
                signInWithPhoneAuthCredential(credential,registered,firstname,lastname,email,phoneNumber!!,username)
            }
            else{
                Toast.makeText(this@OTPverification,"Enter 6 digit OTP",Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential,  registered: Boolean = false, firstname: String? = null, lastname: String? = null, email: String? = null, phoneNumber: String? = null, username: String? = null) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
//                    val user = task.result?.user
                    if(registered==false) {
                        var username2: String? = null
                        if (username == null) {
                            username2 = firstname.toString() + lastname.toString()
                        } else username2 = username

                        val user = hashMapOf(
                            "displayName" to username2.toString(),
                            "email" to email.toString(),
                            "name" to hashMapOf<String, String>(
                                "firstname" to firstname.toString(),
                                "lastname" to lastname.toString()
                            ),
                            "phone" to phoneNumber.toString(),
                            "passyear" to "",
                        )

                        val database = Firebase.firestore
                        database.collection("user").document(phoneNumber.toString()).set(user)
                            .addOnSuccessListener {
                                Log.d("TAG","data uploaded on firestore")
                                Toast.makeText(this,"You are logged in ${firstname}",Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainScreen::class.java)
                                Log.d("TAG", "signInWithCredential:success")
                                startActivity(intent)
                                finishAffinity()
                            }
                            .addOnFailureListener { e ->
                                Log.d("TAG","data not uploaded on firestore")
                                Toast.makeText(this, "fucked", Toast.LENGTH_SHORT).show()
                            }
                    }
                    else{
                        val intent = Intent(this, MainScreen::class.java)
                        Toast.makeText(this,"Welome back $username",Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "signInWithCredential:success")
                        startActivity(intent)
                        finishAffinity()
                    }


                    // ...
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this@OTPverification,"Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                    // ...
                }
            }
    }
}

