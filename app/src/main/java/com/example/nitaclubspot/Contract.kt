package com.example.nitaclubspot

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract


class Contract: ActivityResultContract<Intent, Intent?>() {
    override fun createIntent(context: Context, input: Intent): Intent {
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? {

        if(resultCode != RESULT_OK){
            Log.d("TAG", "user cancelled the intent")
            return null
        }
        else{
            if (intent != null) {
                Log.d("TAG", "this is username after intent call ${
                    intent.getStringExtra("username").toString()
                }")
                return intent
            }
            else return null
        }
    }
}