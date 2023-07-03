package com.dnimara.cinemalink.ui.moviescreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnimara.cinemalink.ui.feedscreen.EditDto
import com.dnimara.cinemalink.ui.reviewscreen.AddReviewDto
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import com.dnimara.cinemalink.ui.watchlistscreens.WatchlistItemDto
import com.dnimara.cinemalink.utils.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class MovieViewModel: ViewModel() {

    private val _movie = MutableLiveData<MovieDetailsDto>()
    val movie: LiveData<MovieDetailsDto>
        get() = _movie

    private val _watchlists = MutableLiveData<List<WatchlistItemDto>>()
    val watchlist: LiveData<List<WatchlistItemDto>>
        get() = _watchlists

    private val _selectedWatchlist = MutableLiveData<Long?>(null)
    val selectedWatchList: LiveData<Long?>
        get() = _selectedWatchlist

    private val _eventNetworkError = MutableLiveData<String?>()
    val eventNetworkError: LiveData<String?>
        get() = _eventNetworkError

    private fun showMovieInfoRequest(movieId: String) {
        viewModelScope.launch {
            try {
                val searchMovieResult = CinemaLinkApi.retrofitService.getMovieById(
                    SessionManager.mInstance.getToken()!!, movieId)
                _movie.value = searchMovieResult
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun showInfo(movieId: String) {
        showMovieInfoRequest(movieId)
    }

    private fun reportAvailabilityRequest(availabilityReportDto: AvailabilityReportDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.reportAvailability(
                    SessionManager.mInstance.getToken()!!, availabilityReportDto)
                showMovieInfoRequest(availabilityReportDto.movieId!!)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun reportAvailability(availabilityReportDto: AvailabilityReportDto) {
        reportAvailabilityRequest(availabilityReportDto)
    }

    private fun rateMovieRequest(rateMovieDto: RateMovieDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.rateMovie(
                    SessionManager.mInstance.getToken()!!, rateMovieDto)
                showMovieInfoRequest(rateMovieDto.movieId)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun rateMovie(rateMovieDto: RateMovieDto) {
        rateMovieRequest(rateMovieDto)
    }

    private fun unrateMovieRequest(movieId: String) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.unrateMovie(
                    SessionManager.mInstance.getToken()!!, movieId)
                showMovieInfoRequest(movieId)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun unrateMovie(movieId: String) {
        unrateMovieRequest(movieId)
    }

    private fun updateMovieForCollectionRequest(movieId: String, ownedStatusList: List<OwnedStatus>) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.updateMovieForCollection(
                    SessionManager.mInstance.getToken()!!,
                    AddMovieToCollectionDto(movieId, ownedStatusList))
                showMovieInfoRequest(movieId)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun updateMovieForCollection(movieId: String, ownedStatusList: List<OwnedStatus>) {
        updateMovieForCollectionRequest(movieId, ownedStatusList)
    }

    private fun getWatchlistsRequest() {
        viewModelScope.launch {
            try {
                val userWatchlists = CinemaLinkApi.retrofitService.getUserWatchlistsForChoice(
                    SessionManager.mInstance.getToken()!!)
                _watchlists.value = userWatchlists
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    private fun createWatchlistRequest(name: String) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.createWatchlist(
                    SessionManager.mInstance.getToken()!!, name)
                getWatchlistsRequest()
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun createWatchlist(name: String) {
        createWatchlistRequest(name)
    }

    private fun addMovieToWatchlistRequest(addMovieToWatchlistDto: AddMovieToWatchlistDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.addMovieToWatchlist(
                    SessionManager.mInstance.getToken()!!, addMovieToWatchlistDto)
            } catch (e: HttpException) {
                showError(e.response()?.errorBody()?.string()!!)
            }
        }
    }

    fun addMovieToWatchlist(addMovieToWatchlistDto: AddMovieToWatchlistDto) {
        addMovieToWatchlistRequest(addMovieToWatchlistDto)
    }

    private fun addReviewRequest(addReviewDto: AddReviewDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.addReview(
                    SessionManager.mInstance.getToken()!!, addReviewDto)
                showMovieInfoRequest(_movie.value?.id!!)
            } catch (e: HttpException) {
                showError(e.response()?.errorBody()?.string()!!)
            }
        }
    }

    fun addReview(addReviewDto: AddReviewDto) {
        addReviewRequest(addReviewDto)
    }

    private fun editReviewRequest(id: Long, editDto: EditDto) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.editReview(
                    SessionManager.mInstance.getToken()!!, id, editDto)
                showMovieInfoRequest(_movie.value?.id!!)
            } catch (e: java.lang.Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun editReviewReq(id: Long, editDto: EditDto) {
        editReviewRequest(id, editDto)
    }

    private fun deleteReviewRequest(id: Long) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.deleteReview(
                    SessionManager.mInstance.getToken()!!, id)
                showMovieInfoRequest(_movie.value?.id!!)
            } catch (e: java.lang.Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun deleteReviewReq(id: Long) {
        deleteReviewRequest(id)
    }

    fun selectWatchlist(id: Long) {
        _selectedWatchlist.value = id
    }

    fun unselectWatchlist() {
        _selectedWatchlist.value = null
    }

    fun showError(message: String) {
        _eventNetworkError.value = message
    }

    fun showErrorDone() {
        _eventNetworkError.value = null
    }

}