package com. example.nitaclubspot

import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.nitaclubspot.BasicDetailsAfterOneTapLogin
import com.example.nitaclubspot.Contract
import com.example.nitaclubspot.MainScreen
import com.example.nitaclubspot.OTPverification
import com.example.nitaclubspot.R
import com.example.nitaclubspot.databinding.ActivityBasicDetailsInputBinding
import com.example.nitaclubspot.databinding.ActivityLoginBinding

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    // Firebase Auth variables
    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseFirestore

    //google auth variables
    private lateinit var googletoken: AuthCredential

    private lateinit var googleSignInClient: GoogleSignInClient
    val contract= registerForActivityResult(Contract()){
        val task = GoogleSignIn.getSignedInAccountFromIntent(it)
        try {
            auth.signInWithCredential(GoogleAuthProvider.getCredential(task.result?.idToken, null))
                .addOnSuccessListener {
                    if (it.additionalUserInfo?.isNewUser == true) {
                        Log.d("TAG", "user is not registered")
                        googletoken = GoogleAuthProvider.getCredential(task.result?.idToken, null)
                        var intent = Intent(this, BasicDetailsAfterOneTapLogin::class.java)
                        Log.d("TAG", "Called intent for more information")
                        intent.putExtra("username", task.result?.displayName.toString())
                        moredetailscontract.launch(intent)
                    } else {
                        database.collection("user").document(auth.currentUser!!.uid)
                            .get()
                            .addOnSuccessListener {
                                if(it.data?.get("firstname") !=null && it.data?.get("lastname")!=null && it.data?.get("phone")!=null && it.data?.get("passyear")!=null){
                                    Log.d("TAG", "user is already registered")
                                    intent = Intent(this, MainScreen::class.java)
                                    Toast.makeText(this, "Welome back ${task.result?.displayName}", Toast.LENGTH_SHORT).show()
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    Log.d("TAG", "user is not registered")
                                    googletoken = GoogleAuthProvider.getCredential(task.result?.idToken, null)
                                    var intent = Intent(this, BasicDetailsAfterOneTapLogin::class.java)
                                    Log.d("TAG", "Called intent for more information")
                                    intent.putExtra("username", task.result?.displayName.toString())
                                    moredetailscontract.launch(intent)
                                }
                            }
                            .addOnFailureListener{
                                Toast.makeText(this,"Error Occured in internet",Toast.LENGTH_SHORT).show()
                            }


                    }

                }
        }
        catch (e:Exception){
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
        }
    }

    //more details contract
    val moredetailscontract = registerForActivityResult(Contract()){
        Log.d("TAG", "this is username after intent call ${
            it?.getStringExtra("username").toString()
        }")

        if (it != null) {
            //Registering user on firebase auth

            progressDialogue.dismiss()
                    if(it.getStringExtra("firstname")!=null && it.getStringExtra("lastname")!=null && it.getStringExtra("phone")!=null){
                        val firebaseUser = auth.currentUser
                        val uid = firebaseUser!!.uid
                        val email = firebaseUser.email.toString()
                        val username = firebaseUser.displayName.toString()
                        Log.d("TAG","user registered on firebase auth")

                        //uploading data on firestore
                        Log.d("TAG","data upload on firestore")
                        val user = hashMapOf(
                            "displayName" to it.getStringExtra("username"),
                            "email" to email,
                            "name"  to hashMapOf<String,String>(
                                "firstname" to it.getStringExtra("firstname").toString(),
                                "lastname"  to it.getStringExtra("lastname").toString()
                            ),
                            "phone" to it.getStringExtra("phone").toString(),
                            "passyear" to it.getStringExtra("passyear").toString(),
                        )
                        val firstname = it.getStringExtra("firstname")
                        database.collection("user").document(auth.currentUser!!.uid).set(user)
                            .addOnSuccessListener {
                                Log.d("TAG","data uploaded on firestore")
                                Toast.makeText(this,"You are logged in ${firstname}",Toast.LENGTH_SHORT).show()
                                intent = Intent(this, MainScreen::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.d("TAG","data not uploaded on firestore")
                                Toast.makeText(this, "fucked", Toast.LENGTH_SHORT).show()
                            }
                    }
                    else{
                        Toast.makeText(this,"Please Fill All Details",Toast.LENGTH_SHORT).show()
                        //firebase logout
                        Firebase.auth.signOut()

                        //google logout
                        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                    }
        }
        else{
            Toast.makeText(this,"User pressed back",Toast.LENGTH_SHORT).show()
            Toast.makeText(this,"Please Fill All Details",Toast.LENGTH_SHORT).show()
            //firebase logout
            Firebase.auth.signOut()

            //google logout
            GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
        }
    }

    private lateinit var progressDialogue: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Auth
        //<----------------------------------------------------------------------->
        auth = FirebaseAuth.getInstance()
        database = Firebase.firestore
        //<----------------------------------------------------------------------->



        //google auth progess dialogue
        //<----------------------------------------------------------------------->
        progressDialogue= ProgressDialog(this)
        progressDialogue.setTitle("Logging in")
        progressDialogue.setMessage("Please wait...")
        progressDialogue.setCanceledOnTouchOutside(false)
        //<----------------------------------------------------------------------->


        //google auth
        //<----------------------------------------------------------------------->
        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestProfile()
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)
        binding.google?.setOnClickListener(){
            val intent = googleSignInClient.signInIntent
            contract.launch(intent)

        }
        //<----------------------------------------------------------------------->

        //phone auth

        val phoneNumber = binding.phonenumber

        phoneNumber?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 10 && s.toString()[0]>='6'){
                    binding.sendotp.isEnabled = true
                    binding.sendotp.isClickable = true
                } else {
                    binding.sendotp.isEnabled = false
                    binding.sendotp.isClickable = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.sendotp.isEnabled = false
                binding.sendotp.isClickable = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.sendotp.isEnabled = false
                binding.sendotp.isClickable = false
            }
        })

        binding.sendotp.setOnClickListener(){
//            intent=Intent(this, OTPverification::class.java)
//            intent.putExtra("phonenumber", "+91"+phoneNumber?.text.toString())
            Toast.makeText(this,"+91"+phoneNumber?.text.toString(),Toast.LENGTH_SHORT).show()
//            startActivity(intent)
//            finish()
            val ref=database.collection("user").document("+91"+phoneNumber?.text.toString())
            ref.get()
                .addOnSuccessListener {
                    val docsnap=it
                    if(it.exists()){
                        Log.d("TAG","user is already registered")
//                        Toast.makeText(this,"Welome back ${docsnap.data?.get("displayName")}",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, OTPverification::class.java)
                        intent.putExtra("phonenumber", "+91"+phoneNumber?.text.toString())
                        intent.putExtra("username", docsnap.data?.get("displayName").toString())
                        startActivity(intent)
                    }
                    else{
                        showDialogue("+91"+phoneNumber?.text.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Internet Error Occured",Toast.LENGTH_SHORT).show()
                }

//            showDialogue("+91"+phoneNumber?.text.toString())
        }


        //<----------------------------------------------------------------------->


    }

    class enabler(
        var firstname: Boolean = false,
        var lastname: Boolean = false,
        var email: Boolean = false,
        var username: Boolean = false
    ){
        fun enable(): Boolean{
            return firstname && lastname
        }
    }

    fun showDialogue(phoneNumber:String){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_basic_details_input)

//        val binding2 = ActivityBasicDetailsInputBinding.bind(dialog.findViewById<View?>(android.R.id.content).rootView)

        val enable= enabler()

        val firstname= dialog.findViewById<EditText>(R.id.firstName)
        val lastname= dialog.findViewById<EditText>(R.id.lastName)
        val sendOTP= dialog.findViewById<Button>(R.id.sendOTP)
        val email = dialog.findViewById<EditText>(R.id.email)
        val phone= dialog.findViewById<EditText>(R.id.phone)
        val username = dialog.findViewById<EditText>(R.id.username)


        firstname.afterTextChanged {
            enable.firstname = firstname.text.toString().length in 1..19
                    && firstname.text.toString().matches(Regex("[A-Za-z ]+"))
            sendOTP.isEnabled = enable.enable()
        }

        lastname.afterTextChanged {
            enable.lastname = lastname.text.toString().length in 1..19
                    && lastname.text.toString().matches(Regex("[A-Za-z ]+"))
            sendOTP.isEnabled = enable.enable()
        }

        email.afterTextChanged {
            enable.email = email.text.toString().matches(Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"))
            sendOTP.isEnabled = enable.enable()
        }

        phone.setText(phoneNumber)
        phone.isEnabled = false

//        val passyear = 2023
//         val passyear = binding2.passoutYear.text
//        Log.d("TAG","passyear is ${passyear}")

        sendOTP.setOnClickListener(){
            intent = Intent(this, OTPverification::class.java)
            intent.putExtra("firstname", firstname.text.toString())
            intent.putExtra("lastname",lastname.text.toString())
            intent.putExtra("phonenumber", phoneNumber)
            intent.putExtra("username", username.text.toString())
            intent.putExtra("email", email.text.toString() )
            intent.putExtra("registered",false)
            startActivity(intent)
        }

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.dialog_animation
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()


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

    }

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */


