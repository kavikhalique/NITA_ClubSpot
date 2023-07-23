package com.example.nitaclubspot.ui.login

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.ViewCompat.animate
import com.example.nitaclubspot.MainScreen
import com.example.nitaclubspot.databinding.ActivityLoginBinding

import com.example.nitaclubspot.R
import com.example.nitaclubspot.data.model.LoggedInUser
import com.example.nitaclubspot.user_signup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    // Firebase Auth variables
    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseDatabase

    //google auth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100

    private lateinit var progressDialogue: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Auth
        //<----------------------------------------------------------------------->
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
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
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)


        binding.google?.setOnClickListener(){
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent,RC_SIGN_IN)
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

    //google auth
    // <----------------------------------------------------------------------->
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account.getIdToken()!!)
            }
            catch (e:Exception){
                Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(idToken: String) {
        progressDialogue.show()
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                progressDialogue.dismiss()
                val firebaseUser = auth.currentUser
                val uid = firebaseUser!!.uid
                val email = firebaseUser!!.email
                val username = firebaseUser!!.displayName
                val user = LoggedInUser(uid,username,email)
                val users = database.getReference("users")
                users.child(uid).setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this,"You are logged in",Toast.LENGTH_SHORT).show()
                        val pref = getSharedPreferences("login", MODE_PRIVATE)
                        val editor = pref.edit()
                        editor.putBoolean("flag",true)
                        editor.putString("username",username)
                        editor.apply()
                        intent = Intent(this, MainScreen::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener{e->
                        Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener{e->
                progressDialogue.dismiss()
                Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            }
    }
    //<-------------------------------------------------------------------------->

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

