package com.dnimara.cinemalink.ui.feedscreen

import androidx.lifecycle.*
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.database.CinemaLinkDatabase
import com.dnimara.cinemalink.domain.ShareDto
import com.dnimara.cinemalink.repositories.FeedRepository
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import com.dnimara.cinemalink.utils.SessionManager
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.lang.Exception

class FeedViewModel(val database: CinemaLinkDatabase,
    application: CinemaLinkApplication): AndroidViewModel(application) {

    private val feedRepository = FeedRepository(CinemaLinkDatabase.getDatabase(application))
    val feed = feedRepository.posts

    private val _loadingScreen = MutableLiveData(true)
    val loadingScreen: LiveData<Boolean>
        get() = _loadingScreen

    private val _navigateToUserProfile = MutableLiveData<Long?>()
    val navigateToUserProfile: LiveData<Long?>
        get() = _navigateToUserProfile

    private val _navigateToSelectedMovie = MutableLiveData<String?>()
    val navigateToSelectedMovie: LiveData<String?>
        get() = _navigateToSelectedMovie

    private val _navigateToPostScreen = MutableLiveData<Long?>()
    val navigateToPostScreen: LiveData<Long?>
        get() = _navigateToPostScreen

    private val _editPost = MutableLiveData<Long?>()
    val editPost: LiveData<Long?>
        get() = _editPost

    private val _editContent = MutableLiveData<String?>()
    val editContent: LiveData<String?>
        get() = _editContent

    private val _shareContent = MutableLiveData<ShareDto?>()
    val shareContent: LiveData<ShareDto?>
        get() = _shareContent

    private val _deletePost = MutableLiveData<Long?>()
    val deletePost: LiveData<Long?>
        get() = _deletePost

    init {
        try {
            loading()
            refreshDataFromRepository(SessionManager.mInstance.getToken()!!)
        } catch(e: Exception) {
        }
    }

    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _eventNetworkError = MutableLiveData(false)

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    private fun refreshDataFromRepository(accessToken: String) {
        viewModelScope.launch {
            try {
                feedRepository.refreshFeed(accessToken)
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                if(feed.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

    private fun reactRequest(accessToken: String, likeDto: LikeDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.reactToPost(accessToken, likeDto)
                refreshFeed(accessToken)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun react(postId: Long, liked: Boolean) {
        reactRequest(SessionManager.mInstance.getToken()!!, LikeDto(postId, liked))
    }

    fun refreshFeed(accessToken: String) {
        refreshDataFromRepository(accessToken)
    }

    private fun editPostRequest(id: Long, editDto: EditDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.editPost(
                    SessionManager.mInstance.getToken()!!, id, editDto)
                refreshFeed(SessionManager.mInstance.getToken()!!)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun editPostReq(id: Long, editDto: EditDto) {
        editPostRequest(id, editDto)
    }

    private fun deletePostRequest(id: Long) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.deletePost(
                    SessionManager.mInstance.getToken()!!, id)
                refreshFeed(SessionManager.mInstance.getToken()!!)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun deletePostReq(id: Long) {
        deletePostRequest(id)
    }

    fun loading() {
        _loadingScreen.value = true
    }

    fun loadingComplete() {
        _loadingScreen.value = false
    }

    fun share(shareDto: ShareDto) {
        _shareContent.value = shareDto
    }

    fun shareComplete() {
        _shareContent.value = null
    }

    fun navigateToUserProfile(userId: Long) {
        _navigateToUserProfile.value = userId
    }

    fun navigateToUserProfileComplete() {
        _navigateToUserProfile.value = null
    }

    fun navigateToPost(postId: Long) {
        _navigateToPostScreen.value = postId
    }

    fun navigateToPostComplete() {
        _navigateToPostScreen.value = null
    }

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    fun editPost(postId: Long, content: String) {
        _editContent.value = content
        _editPost.value = postId
    }

    fun editPostComplete() {
        _editPost.value = null
        _editContent.value = null
    }

    fun deletePost(postId: Long) {
        _deletePost.value = postId
    }

    fun deletePostComplete() {
        _deletePost.value = null
    }

    fun displayMovie(movieId: String) {
        _navigateToSelectedMovie.value = movieId
    }

    fun displayMovieComplete() {
        _navigateToSelectedMovie.value = null
    }

}