package com.dnimara.cinemalink.ui.profilescreen

import androidx.lifecycle.*
import com.dnimara.cinemalink.domain.ShareDto
import com.dnimara.cinemalink.ui.feedscreen.LikeDto
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import com.dnimara.cinemalink.utils.SessionManager
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import java.lang.Exception
import kotlin.properties.Delegates

class ProfileViewModel: ViewModel() {

    private val _profileInfo = MutableLiveData<ProfileDto>()
    val profileInfo: LiveData<ProfileDto>
        get() = _profileInfo
    private var userIdProfile by Delegates.notNull<Long>()

    private var _showButtons = MutableLiveData(false)
    val showButtons: LiveData<Boolean>
        get() = _showButtons

    private val _navigateToSelectedMovie = MutableLiveData<String?>()
    val navigateToSelectedMovie: LiveData<String?>
        get() = _navigateToSelectedMovie

    private val _navigateToSelectedUserProfile = MutableLiveData<Long?>()
    val navigateToSelectedUserProfile: LiveData<Long?>
        get() = _navigateToSelectedUserProfile

    private val _navigateToPostScreen = MutableLiveData<Long?>()
    val navigateToPostScreen: LiveData<Long?>
        get() = _navigateToPostScreen

    private val _shareContent = MutableLiveData<ShareDto?>()
    val shareContent: LiveData<ShareDto?>
        get() = _shareContent

    private fun getProfileInfoRequest(userId: Long) {
        viewModelScope.launch {
            try {
                val profileInfo = CinemaLinkApi.retrofitService.getUserProfile(
                    SessionManager.mInstance.getToken()!!, userId)
                _profileInfo.value = profileInfo
                userIdProfile = userId
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }

    }

    fun getProfileInfo(userId: Long) {
        getProfileInfoRequest(userId)
    }

    private fun followUserRequest() {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.followUser(SessionManager.mInstance.getToken()!!,
                    userIdProfile)
                getProfileInfoRequest(userIdProfile)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun followUser() {
        followUserRequest()
    }

    private fun updateProfilePicRequest(profilePicture: File) {
        viewModelScope.launch {
            try {
                var filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "profilePicture", profilePicture.name, RequestBody.create(
                        MediaType.parse("image"), profilePicture
                    )
                )
                CinemaLinkApi.retrofitService.updateProfilePic(
                    SessionManager.mInstance.getToken()!!, filePart)
                getProfileInfoRequest(userIdProfile)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun updateProfilePic(profilePicture: File) {
        updateProfilePicRequest(profilePicture)
    }

    private fun updateUsernameRequest(username: String) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.updateUsername(
                    SessionManager.mInstance.getToken()!!, username)
                getProfileInfoRequest(userIdProfile)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun updateUsername(username: String) {
        updateUsernameRequest(username)
    }

    private fun reactRequest(accessToken: String, likeDto: LikeDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.reactToPost(accessToken, likeDto)
                getProfileInfoRequest(userIdProfile)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun react(postId: Long, liked: Boolean) {
        reactRequest(SessionManager.mInstance.getToken()!!, LikeDto(postId, liked))
    }

    fun showButtons() {
        _showButtons.value = true
    }

    fun hideButtons() {
        _showButtons.value = false
    }

    fun displayMovie(movieId: String) {
        _navigateToSelectedMovie.value = movieId
    }

    fun displayMovieComplete() {
        _navigateToSelectedMovie.value = null
    }

    fun displayUserProfile(userId: Long) {
        _navigateToSelectedUserProfile.value = userId
    }

    fun displayUserProfileComplete() {
        _navigateToSelectedUserProfile.value = null
    }

    fun navigateToPost(postId: Long) {
        _navigateToPostScreen.value = postId
    }

    fun navigateToPostComplete() {
        _navigateToPostScreen.value = null
    }

    fun share(shareDto: ShareDto) {
        _shareContent.value = shareDto
    }

    fun shareComplete() {
        _shareContent.value = null
    }

}