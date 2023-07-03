package com.dnimara.cinemalink.ui.watchlistscreens

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.database.CinemaLinkDatabase
import com.dnimara.cinemalink.repositories.WatchlistRepository
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import com.dnimara.cinemalink.utils.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class WatchlistsViewModel(val database: CinemaLinkDatabase,
    application: CinemaLinkApplication): AndroidViewModel(application) {

    private val watchlistRepository = WatchlistRepository(CinemaLinkDatabase.getDatabase(application))
    val watchlists = watchlistRepository.watchlists

    private val _navigateToSelectedWatchlist = MutableLiveData<Long?>()
    val navigateToSelectedWatchlist: LiveData<Long?>
        get() = _navigateToSelectedWatchlist

    private val _eventNetworkError = MutableLiveData<String?>()
    val eventNetworkError: LiveData<String?>
        get() = _eventNetworkError

    init {
        try {
            refreshDataFromRepository()
        } catch(e: HttpException) {
        }
    }

    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                watchlistRepository.refreshWatchlists()
            } catch (e: HttpException) {
                Timber.e("${e.message}")
            }
        }
    }

    fun refreshWatchlists() {
        refreshDataFromRepository()
    }

    private fun createWatchlistRequest(name: String) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.createWatchlist(
                    SessionManager.mInstance.getToken()!!, name)
                refreshDataFromRepository()
            } catch (e: HttpException) {
                showError(e.response()?.errorBody()?.string()!!)
            }
        }
    }

    fun createWatchlist(name: String) {
        createWatchlistRequest(name)
    }

    fun showWatchlist(watchlistId: Long) {
        _navigateToSelectedWatchlist.value = watchlistId
    }

    fun showWatchlistComplete() {
        _navigateToSelectedWatchlist.value = null
    }

    fun showError(message: String) {
        _eventNetworkError.value = message
    }

    fun showErrorDone() {
        _eventNetworkError.value = null
    }

}