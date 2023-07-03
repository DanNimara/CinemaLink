package com.dnimara.cinemalink.ui.reviewscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnimara.cinemalink.ui.feedscreen.EditDto
import com.dnimara.cinemalink.ui.feedscreen.LikeDto
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import com.dnimara.cinemalink.utils.SessionManager
import kotlinx.coroutines.launch
import timber.log.Timber

class ReviewViewModel: ViewModel() {

    private val _review = MutableLiveData<ReviewCommentsDto>()
    val review: LiveData<ReviewCommentsDto>
        get() = _review

    private val _navigateToUserProfile = MutableLiveData<Long?>()
    val navigateToUserProfile: LiveData<Long?>
        get() = _navigateToUserProfile

    private val _editComment = MutableLiveData<Long?>()
    val editComment: LiveData<Long?>
        get() = _editComment

    private val _editContent = MutableLiveData<String?>()
    val editContent: LiveData<String?>
        get() = _editContent

    private val _deleteComment = MutableLiveData<Long?>()
    val deleteComment: LiveData<Long?>
        get() = _deleteComment

    private fun getReviewRequest(reviewId: Long) {
        viewModelScope.launch {
            try {
                val reviewResult = CinemaLinkApi.retrofitService.getReview(
                    SessionManager.mInstance.getToken()!!, reviewId)
                _review.value = reviewResult
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun getReview(reviewId: Long) {
        getReviewRequest(reviewId)
    }

    private fun addCommentRequest(reviewId: Long, content: String) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.addReviewComment(
                    SessionManager.mInstance.getToken()!!, reviewId, EditDto(content)
                )
                getReview(_review.value?.id!!)
            } catch (e: java.lang.Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun addComment(reviewId: Long, content: String) {
        addCommentRequest(reviewId, content)
    }

    private fun reactToCommentRequest(likeDto: LikeDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.reactToReviewComment(
                    SessionManager.mInstance.getToken()!!, likeDto)
                getReview(_review.value?.id!!)
            } catch (e: java.lang.Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun reactToComment(commentId: Long, liked: Boolean) {
        reactToCommentRequest(LikeDto(commentId, liked))
    }

    private fun reactToReviewRequest(likeDto: LikeDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.reactToReview(
                    SessionManager.mInstance.getToken()!!, likeDto)
                getReview(_review.value?.id!!)
            } catch (e: java.lang.Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun reactToReview(reviewId: Long, liked: Boolean) {
        reactToReviewRequest(LikeDto(reviewId, liked))
    }

    private fun editCommentRequest(id: Long, editDto: EditDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.editReviewComment(
                    SessionManager.mInstance.getToken()!!, id, editDto)
                getReview(_review.value?.id!!)
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
                CinemaLinkApi.retrofitService.deleteReviewComment(
                    SessionManager.mInstance.getToken()!!, id)
                getReview(_review.value?.id!!)
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

}