package com.dnimara.cinemalink.ui.searchuserscreen

data class UserDto(val userId: Long,
                   val username: String,
                   val profilePictureUrl: String,
                   val followStatus: Boolean?)
