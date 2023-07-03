package com.dnimara.cinemalink.ui.reviewscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnimara.cinemalink.domain.ShareDto
import com.dnimara.cinemalink.ui.collectionscreen.CollectionDto
import com.dnimara.cinemalink.ui.feedscreen.LikeDto
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import com.dnimara.cinemalink.utils.SessionManager
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class ReviewsViewModel: ViewModel() {

    private val _reviews = MutableLiveData<List<ReviewDto>>()
    val reviews: LiveData<List<ReviewDto>>
        get() = _reviews

    private val _navigateToUserProfile = MutableLiveData<Long?>()
    val navigateToUserProfile: LiveData<Long?>
        get() = _navigateToUserProfile

    private val _navigateToSelectedMovie = MutableLiveData<String?>()
    val navigateToSelectedMovie: LiveData<String?>
        get() = _navigateToSelectedMovie

    private val _navigateToReviewScreen = MutableLiveData<Long?>()
    val navigateToPostScreen: LiveData<Long?>
        get() = _navigateToReviewScreen

    private val _shareContent = MutableLiveData<ShareDto?>()
    val shareContent: LiveData<ShareDto?>
        get() = _shareContent

    private fun refreshReviewsRequest(movieId: String) {
        viewModelScope.launch {
            try {
                val reviewsResult = CinemaLinkApi.retrofitService.getUserReviews(
                    SessionManager.mInstance.getToken()!!, movieId)
                _reviews.value = reviewsResult
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun refreshReviews(movieId: String) {
        refreshReviewsRequest(movieId)
    }

    private fun reactRequest(accessToken: String, movieId: String, likeDto: LikeDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.reactToReview(accessToken, likeDto)
                refreshReviews(movieId)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun react(postId: Long, movieId: String, liked: Boolean) {
        reactRequest(SessionManager.mInstance.getToken()!!, movieId, LikeDto(postId, liked))
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

    fun navigateToReview(reviewId: Long) {
        _navigateToReviewScreen.value = reviewId
    }

    fun navigateToReviewComplete() {
        _navigateToReviewScreen.value = null
    }

}