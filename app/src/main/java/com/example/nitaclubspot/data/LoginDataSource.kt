package com.example.nitaclubspot.data

import com.example.nitaclubspot.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication

            if(username=="kavikhalique"&& password=="admin123") {
                val papa = LoggedInUser("8u8bjnb","Kavi Khalique","abc@xyz.com")
                return Result.Success(papa)
            }

//            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            return Result.Error(object: java.lang.Exception(){})
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}