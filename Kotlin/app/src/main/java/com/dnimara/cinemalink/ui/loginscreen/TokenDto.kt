package com.dnimara.cinemalink.ui.loginscreen

import com.squareup.moshi.Json

data class TokenDto(@Json(name = "access_token") val accessToken: String,
                    @Json(name = "expires_in") val expiresIn: Int,
                    @Json(name = "refresh_expires_in") val refreshExpiresIn: Int,
                    @Json(name = "refresh_token") val refreshToken: String,
                    @Json(name = "token_type") val tokenType: String,
                    @Json(name = "not-before-policy") val notBeforePolicy: Int,
                    @Json(name = "session_state") val sessionState: String,
                    val scope: String)
