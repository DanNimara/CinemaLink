package com.dnimara.cinemalink.ui.profilescreen

import com.dnimara.cinemalink.database.PostDto
import com.dnimara.cinemalink.ui.searchuserscreen.UserDto

data class ProfileDto(val userId: Long,
                    val username: String,
                    val profilePictureUrl: String,
                    val allowFollow: Boolean,
                    val followed: Boolean,
                    val following: Long,
                    val followingUsers: List<UserDto>?,
                    val followers: Long,
                    val followerUsers: List<UserDto>?,
                    val posts: List<PostDto>?)
