package com.dnimara.cinemalink.ui.feedscreen

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatePostRequest(val content: String,
                            val tagIds: List<String>)