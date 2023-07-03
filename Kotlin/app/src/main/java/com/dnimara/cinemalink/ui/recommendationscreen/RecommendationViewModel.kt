package com.dnimara.cinemalink.ui.recommendationscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import com.dnimara.cinemalink.utils.SessionManager
import kotlinx.coroutines.launch
import timber.log.Timber

class RecommendationViewModel: ViewModel() {

    private val _recommendations = MutableLiveData<List<RecommendationDto>>()
    val recommendations: LiveData<List<RecommendationDto>>
        get() = _recommendations

    private val _navigateToSelectedMovie = MutableLiveData<String?>()
    val navigateToSelectedMovie: LiveData<String?>
        get() = _navigateToSelectedMovie

    private fun getRecommendationsRequest() {
        viewModelScope.launch {
            try {
                val recommendationsResult = CinemaLinkApi.retrofitService
                    .getUserMovieRecommendations(SessionManager.mInstance.getToken()!!)
                _recommendations.value = recommendationsResult
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }

    }

    fun getRecommendations() {
        getRecommendationsRequest()
    }

    fun displayMovie(movieId: String) {
        _navigateToSelectedMovie.value = movieId
    }

    fun displayMovieComplete() {
        _navigateToSelectedMovie.value = null
    }

}