package com.example.nitaclubspot.ui.login

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.LongDef
import com.example.nitaclubspot.BasicDetailsAfterOneTapLogin
import com.example.nitaclubspot.MainScreen
import com.example.nitaclubspot.databinding.ActivityLoginBinding

import com.example.nitaclubspot.R
import com.example.nitaclubspot.data.model.Contract
import com.example.nitaclubspot.user_signup
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.contracts.contract

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    // Firebase Auth variables
    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseFirestore

    //google auth variables
    private lateinit var googletoken: AuthCredential

    private lateinit var googleSignInClient: GoogleSignInClient
    val contract= registerForActivityResult(Contract()){
        val task = GoogleSignIn.getSignedInAccountFromIntent(it)
        try{
            val account = task.getResult(ApiException::class.java)
            val ref=database.collection("user").document(account?.email.toString())
            ref.get()
                .addOnSuccessListener {
                    val docsnap=it
                    if(it.exists()){
                        Log.d("TAG","user is already registered")

                        //authorizing firebase
                        auth.signInWithCredential(GoogleAuthProvider.getCredential(account.idToken,null))
                            .addOnSuccessListener {
                                intent = Intent(this, MainScreen::class.java)
                                Toast.makeText(this,"Welome back ${docsnap.data?.get("displayName")}",Toast.LENGTH_SHORT).show()
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this,"Error Occured",Toast.LENGTH_SHORT).show()
                            }

                    }
                    else{
                        googletoken=GoogleAuthProvider.getCredential(account?.idToken,null)
                        var intent = Intent(this,BasicDetailsAfterOneTapLogin::class.java)
                        Log.d("TAG","Called intent for more information")
                        intent.putExtra("username",account?.displayName.toString())
                        moredetailscontract.launch(intent)
                    }
//                    firebaseAuthWithGoogleAccount(account.getIdToken()!!)
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Internet Error Occured",Toast.LENGTH_SHORT).show()
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
            Log.d("TAG","registering user on firebase auth")
            auth.signInWithCredential(googletoken)
                .addOnCompleteListener(this) { authResult ->
                    Log.d("TAG","firebaseAuthWithGoogleAccount: ${authResult.isSuccessful}")
                    progressDialogue.dismiss()
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser!!.uid
                    val email = firebaseUser!!.email.toString()
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
                    database.collection("user").document(email).set(user)
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
                .addOnFailureListener{e->
                    progressDialogue.dismiss()
                    Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
                }
        }
        else{
            Toast.makeText(this,"Error Occured",Toast.LENGTH_SHORT).show()
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

        binding.signup?.setOnClickListener(){
            intent = Intent(this, user_signup::class.java)
            startActivity(intent)
        }


        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        //it continuously observes the viemodel and as login results
        // gets inserted into LoginResult it performs further actions
        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)

                setResult(Activity.RESULT_OK)

                //Complete and destroy login activity once successful

                finish()
            }
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {

                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience

        val pref = getSharedPreferences("login", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("flag",true)
        editor.putString("username",displayName)
        editor.apply()

        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()

        with(window){
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = android.transition.Fade()
            exitTransition =  android.transition.Fade()
        }
        intent = Intent(this, MainScreen::class.java)
        startActivity(intent)

    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }


    }

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })

}

