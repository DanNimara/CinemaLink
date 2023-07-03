package com.dnimara.cinemalink.utils

import android.content.Context
import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.application.CinemaLinkApplication

/**
 * Session manager to save and fetch data from SharedPreferences
 */

class SessionManager(sharedPrefs: SharedPreferences) {

    fun getToken(): String? = sharedPreferences.getString(ACCESS_TOKEN, null)

    fun setToken(token: String) {
        sharedPreferences.edit().putString(ACCESS_TOKEN, token).apply()
    }

    fun unsetToken() {
        sharedPreferences.edit().clear().apply()
    }

    fun getUserId(): Long {
        val jwt = JWT(getToken()!!.replace("Bearer ", ""))
        return jwt.getClaim("preferred_username").asLong()!!
    }

    companion object {
        const val ACCESS_TOKEN = "access_token"
        const val USER_ID = "userId"
        var mInstance: SessionManager = SessionManager(getPrefs())

        private fun getPrefs(): SharedPreferences {
            return CinemaLinkApplication.mInstance.getSharedPreferences(
                "CinemaLinkApplication", Context.MODE_PRIVATE
            )
        }

        fun getInstance(): SessionManager {
            return mInstance
        }
    }

    private var sharedPreferences = sharedPrefs
}