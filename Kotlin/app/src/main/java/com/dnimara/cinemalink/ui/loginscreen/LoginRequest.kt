package com.dnimara.cinemalink.ui.loginscreen

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val email: String, val password: String,
    val grantType: String = "password", val clientId: String = "viewer",
    val clientSecret: String = "1a39f6a5-8738-4cdd-9cc3-3d710e2d2836")