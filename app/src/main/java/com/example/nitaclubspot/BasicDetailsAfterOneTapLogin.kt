package com.example.nitaclubspot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.nitaclubspot.databinding.ActivityBasicDetailsAfterOneTapLoginBinding

class enabler(
    var firstname: Boolean= false,
    var lastname: Boolean = false,
    var phone: Boolean = true,
    var username: Boolean= true) {

     fun do_enable(): Boolean {
         return (firstname && lastname && phone && username)
     }
 }

class BasicDetailsAfterOneTapLogin : AppCompatActivity() {
    private fun  available_username(username: String?): String {
        return (username?.replace("\\s".toRegex(), "").toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBasicDetailsAfterOneTapLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val signinEnabler=enabler()

        binding.signin.isEnabled = signinEnabler.do_enable()

        val username=intent.getStringExtra("username")
        binding.username.editableText.append(available_username(username))

        binding.firstname.afterTextChanged {
            signinEnabler.firstname = (it.isNotBlank() && isalpha(it))
            if(!isalpha(it)){
                binding.firstname.error="Enter only alphabets"
            }
            binding.signin.isEnabled= signinEnabler.do_enable()
        }

        binding.lastname.afterTextChanged {
            signinEnabler.lastname = (it.isNotBlank() && isalpha(it))
            if(!isalpha(it)){
                binding.lastname.error="Enter only alphabets"
            }
            binding.signin.isEnabled= signinEnabler.do_enable()
        }

        binding.phone.afterTextChanged {
            signinEnabler.phone = (!it.isNotBlank() || it.length==10)
            if(it.length>0 && it.length!=10){
                binding.phone.error="Enter 10 digit phone number"
            }
            binding.signin.isEnabled= signinEnabler.do_enable()
        }

        binding.username.afterTextChanged {
            signinEnabler.username = (it.isNotBlank() && it.length>=6)
            if(it.length<6){
                binding.username.error="Enter atleast 6 characters"
            }
            binding.signin.isEnabled= signinEnabler.do_enable()
        }

        binding.signin.setOnClickListener(){

            intent.putExtra("username",binding.username.text.toString())
            intent.putExtra("firstname",binding.firstname.text.toString())
            intent.putExtra("lastname",binding.lastname.text.toString())
            intent.putExtra("phone",binding.phone.text.toString())
            setResult(RESULT_OK,intent)
            finish()
        }

    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        finish()
    }

    fun isalpha(str: String): Boolean {
        var i = 0
        while (i < str.length) {
            if (!Character.isLetter(str[i])) {
                return false
            }
            i++
        }
        return true
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