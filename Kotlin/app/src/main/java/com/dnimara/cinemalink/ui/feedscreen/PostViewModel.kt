package com.dnimara.cinemalink.ui.feedscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import com.dnimara.cinemalink.utils.SessionManager
import kotlinx.coroutines.launch
import timber.log.Timber

class PostViewModel: ViewModel() {

    private val _post = MutableLiveData<PostCommentsDto>()
    val post: LiveData<PostCommentsDto>
        get() = _post

    private val _navigateToUserProfile = MutableLiveData<Long?>()
    val navigateToUserProfile: LiveData<Long?>
        get() = _navigateToUserProfile

    private val _navigateToSelectedMovie = MutableLiveData<String?>()
    val navigateToSelectedMovie: LiveData<String?>
        get() = _navigateToSelectedMovie

    private val _navigateToFeed = MutableLiveData(false)
    val navigateToFeed: LiveData<Boolean>
        get() = _navigateToFeed

    private val _editComment = MutableLiveData<Long?>()
    val editComment: LiveData<Long?>
        get() = _editComment

    private val _editContent = MutableLiveData<String?>()
    val editContent: LiveData<String?>
        get() = _editContent

    private val _deleteComment = MutableLiveData<Long?>()
    val deleteComment: LiveData<Long?>
        get() = _deleteComment

    private fun getPostRequest(postId: Long) {
        viewModelScope.launch {
            try {
                val postResult = CinemaLinkApi.retrofitService.getPost(
                    SessionManager.mInstance.getToken()!!, postId)
                _post.value = postResult
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun getPost(postId: Long) {
        getPostRequest(postId)
    }

    private fun addCommentRequest(postId: Long, content: String) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.addComment(
                    SessionManager.mInstance.getToken()!!, postId, EditDto(content)
                )
                getPost(_post.value?.postId!!)
            } catch (e: java.lang.Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun addComment(postId: Long, content: String) {
        addCommentRequest(postId, content)
    }

    private fun reactToCommentRequest(likeDto: LikeDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.reactToComment(
                    SessionManager.mInstance.getToken()!!, likeDto)
                getPost(_post.value?.postId!!)
            } catch (e: java.lang.Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun reactToComment(commentId: Long, liked: Boolean) {
        reactToCommentRequest(LikeDto(commentId, liked))
    }

    private fun reactToPostRequest(likeDto: LikeDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.reactToPost(
                    SessionManager.mInstance.getToken()!!, likeDto)
                getPost(_post.value?.postId!!)
            } catch (e: java.lang.Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun reactToPost(postId: Long, liked: Boolean) {
        reactToPostRequest(LikeDto(postId, liked))
    }

    private fun editPostRequest(id: Long, editDto: EditDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.editPost(
                    SessionManager.mInstance.getToken()!!, id, editDto)
                getPost(_post.value?.postId!!)
            } catch (e: java.lang.Exception) {
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
                getPost(_post.value?.postId!!)
            } catch (e: java.lang.Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun deletePostReq(id: Long) {
        deletePostRequest(id)
    }

    private fun editCommentRequest(id: Long, editDto: EditDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.editComment(
                    SessionManager.mInstance.getToken()!!, id, editDto)
                getPost(_post.value?.postId!!)
            } catch (e: java.lang.Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun editCommentReq(id: Long, editDto: EditDto) {
        editCommentRequest(id, editDto)
    }

    private fun deleteCommentRequest(id: Long) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.deleteComment(
                    SessionManager.mInstance.getToken()!!, id)
                getPost(_post.value?.postId!!)
            } catch (e: java.lang.Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun deleteCommentReq(id: Long) {
        deleteCommentRequest(id)
    }

    fun navigateToUserProfile(userId: Long) {
        _navigateToUserProfile.value = userId
    }

    fun navigateToUserProfileComplete() {
        _navigateToUserProfile.value = null
    }

    fun navigateToFeed() {
        _navigateToFeed.value = true
    }

    fun navigateToFeedComplete() {
        _navigateToFeed.value = false
    }

    fun editComment(commentId: Long, content: String) {
        _editContent.value = content
        _editComment.value = commentId
    }

    fun editCommentComplete() {
        _editComment.value = null
        _editContent.value = null
    }

    fun deleteComment(commentId: Long) {
        _deleteComment.value = commentId
    }

    fun deleteCommentComplete() {
        _deleteComment.value = null
    }

    fun displayMovie(movieId: String) {
        _navigateToSelectedMovie.value = movieId
    }

    fun displayMovieComplete() {
        _navigateToSelectedMovie.value = null
    }

}