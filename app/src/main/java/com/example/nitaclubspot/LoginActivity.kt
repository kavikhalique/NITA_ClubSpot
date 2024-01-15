package com. example.nitaclubspot

import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
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
import android.view.KeyEvent
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
import com.example.nitaclubspot.databinding.ActivityOtpverificationBinding

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
import io.grpc.okhttp.internal.Credentials
import java.util.concurrent.TimeUnit

    var user = UserDetails("not_set","not_set","not_set","not_set","not_set")

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

        binding.sendotp.setOnClickListener{
            Toast.makeText(this,"+91"+phoneNumber?.text.toString(),Toast.LENGTH_SHORT).show()
            OTPverification("+91"+(phoneNumber?.text.toString()))
        }

//        binding.sendotp.setOnClickListener(){
////            intent=Intent(this, OTPverification::class.java)
////            intent.putExtra("phonenumber", "+91"+phoneNumber?.text.toString())
//            Toast.makeText(this,"+91"+phoneNumber?.text.toString(),Toast.LENGTH_SHORT).show()
////            startActivity(intent)
////            finish()
//            val ref=database.collection("user").document("+91"+phoneNumber?.text.toString())
//            ref.get()
//                .addOnSuccessListener {
//                    val docsnap=it
//                    if(it.exists()){
//                        Log.d("TAG","user is already registered")
////                        Toast.makeText(this,"Welome back ${docsnap.data?.get("displayName")}",Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this, OTPverification::class.java)
//                        intent.putExtra("phonenumber", "+91"+phoneNumber?.text.toString())
//                        intent.putExtra("username", docsnap.data?.get("displayName").toString())
//                        startActivity(intent)
//                    }
//                    else{
//                        showDialogue("+91"+phoneNumber?.text.toString())
//                    }
//                }
//                .addOnFailureListener {
//                    Toast.makeText(this,"Internet Error Occured",Toast.LENGTH_SHORT).show()
//                }
//
////            showDialogue("+91"+phoneNumber?.text.toString())
//        }




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
//           user = UserDetails(firstname.text.toString(),lastname.text.toString(),email.text.toString(),phone.text.toString(),username.text.toString())


            database.collection("user").document(auth.uid!!).set(hashMapOf(
                "displayName" to username.text.toString(),
                "email" to email.text.toString(),
                "name" to hashMapOf(
                    "firstname" to firstname.text.toString(),
                    "lastname" to lastname.text.toString()
                ),
                "phone" to phone.text.toString(),
                "event_liked" to hashMapOf<String,Boolean>()
            ))
                .addOnSuccessListener {
                    Log.d("TAG","user got registered")
                    Toast.makeText(this,"You are logged in ${firstname.text.toString()}",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    intent = Intent(this, MainScreen::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.d("TAG","user is not registered")
                    Toast.makeText(this,"Error Occured",Toast.LENGTH_SHORT).show()
                }
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

    fun OTPverification(phoneNumber:String){

        var storedVerificationId: String? = null
        var resendToken: PhoneAuthProvider.ForceResendingToken? = null

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(ContentValues.TAG, "onVerificationCompleted:$credential")

                loginWithPhoneCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(ContentValues.TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(this@LoginActivity,"Invalid Phone Number", Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(this@LoginActivity,"Quota Exceeded", Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                    Toast.makeText(this@LoginActivity,"Null Activity", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this@LoginActivity,"Error Occured", Toast.LENGTH_SHORT).show()
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

                Log.d("TAG","storedVerificationId is ${storedVerificationId}")

                showOtpDialog(storedVerificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        Log.d("TAG","called phone auth provider with options")
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun showOtpDialog(storedVerificationId: String?){
        Log.d("TAG","called showOtpDialog and now otp enter dialog should open")
        var otp: String = ""
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_otpverification)



//        val binding2 = ActivityOtpverificationBinding.bind(dialog.findViewById<View?>(android.R.id.content).rootView)
        val otp1 = dialog.findViewById<EditText>(R.id.otp1)
        val otp2 = dialog.findViewById<EditText>(R.id.otp2)
        val otp3 = dialog.findViewById<EditText>(R.id.otp3)
        val otp4 = dialog.findViewById<EditText>(R.id.otp4)
        val otp5 = dialog.findViewById<EditText>(R.id.otp5)
        val otp6 = dialog.findViewById<EditText>(R.id.otp6)
        val verify = dialog.findViewById<Button>(R.id.verify)

        otp1.afterTextChanged {
            if(otp1.text.toString().length==1){
                otp2.requestFocus()
                otp+=otp1.text.toString()
            }

        }
        otp2.afterTextChanged {
            if(otp2.text.toString().length==1){
                otp3.requestFocus()
                otp+=otp2.text.toString()
            }

        }
        otp3.afterTextChanged {
            if(otp3.text.toString().length==1){
                otp4.requestFocus()
                otp+=otp3.text.toString()
            }

        }
        otp4.afterTextChanged {
            if(otp4.text.toString().length==1){
                otp5.requestFocus()
                otp+=otp4.text.toString()
            }

        }
        otp5.afterTextChanged {
            if(otp5.text.toString().length==1){
                otp6.requestFocus()
                otp+=otp5.text.toString()
            }

        }
        otp6.afterTextChanged {
            if(otp6.text.toString().length==1){
                otp+=otp6.text.toString()
            }

        }

        otp1.setOnKeyListener(View.OnKeyListener(){ v, keyCode, event ->
            if(keyCode== KeyEvent.KEYCODE_DEL){
                otp1.requestFocus()
                otp=""
            }
            false
        })
        otp2.setOnKeyListener(View.OnKeyListener(){ v, keyCode, event ->
            if(keyCode== KeyEvent.KEYCODE_DEL && otp2.text.toString().length==0){
                otp1.requestFocus()
                otp=otp.substring(0,otp.length-1)
            }
            false
        })
        otp3.setOnKeyListener(View.OnKeyListener(){ v, keyCode, event ->
            if(keyCode== KeyEvent.KEYCODE_DEL && otp3.text.toString().length==0){
                otp2.requestFocus()
                otp=otp.substring(0,otp.length-1)
            }
            false
        })
        otp4.setOnKeyListener(View.OnKeyListener(){ v, keyCode, event ->
            if(keyCode== KeyEvent.KEYCODE_DEL && otp4.text.toString().length==0){
                otp3.requestFocus()
                otp=otp.substring(0,otp.length-1)
            }
            false
        })
        otp5.setOnKeyListener(View.OnKeyListener(){ v, keyCode, event ->
            if(keyCode== KeyEvent.KEYCODE_DEL && otp5.text.toString().length==0){
                otp4.requestFocus()
                otp=otp.substring(0,otp.length-1)
            }
            false
        })
        otp6.setOnKeyListener(View.OnKeyListener(){ v, keyCode, event ->
            if(keyCode== KeyEvent.KEYCODE_DEL && otp6.text.toString().length==0){
                otp5.requestFocus()
                otp=otp.substring(0,otp.length-1)
            }
            else if(keyCode== KeyEvent.KEYCODE_ENTER){
                verify.performClick()
            }
            false
        })

        verify.setOnClickListener{
            if(otp.length==6){
                val credential = PhoneAuthProvider.getCredential(storedVerificationId.toString(), otp)
                loginWithPhoneCredential(credential,dialog)
            }
            else{
                Toast.makeText(this,"Please enter 6 digit OTP",Toast.LENGTH_SHORT).show()
            }
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

    fun loginWithPhoneCredential(credential: PhoneAuthCredential,dialog: Dialog? = null ){
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                database.collection("user").document(auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        if(it.exists()){
                            Log.d("TAG","user is already registered")
                            Toast.makeText(this,"Welome back ${it.data?.get("displayName")}",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainScreen::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Log.d("TAG","user is not registered")
                            showDialogue(auth.currentUser!!.phoneNumber.toString())
                        }
                        if (dialog != null) {
                            dialog.dismiss()
                        }
                    }
                    .addOnFailureListener{
                        Toast.makeText(this,"Error Occured during database checkup",Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener{
                Toast.makeText(this,"Error Occured during authentication",Toast.LENGTH_SHORT).show()
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

    }

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */


