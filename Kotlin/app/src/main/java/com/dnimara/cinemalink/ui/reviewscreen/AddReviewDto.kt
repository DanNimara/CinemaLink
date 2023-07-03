package com.dnimara.cinemalink.ui.reviewscreen

data class AddReviewDto(val movieId: String,
                        val recommended: Boolean,
                        val content: String)
