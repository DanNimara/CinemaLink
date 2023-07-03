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

class ShowWatchlistViewModel(val database: CinemaLinkDatabase,
                             application: CinemaLinkApplication
): AndroidViewModel(application) {

    private val watchlistRepository = WatchlistRepository(CinemaLinkDatabase.getDatabase(application))

    private val _watchlist = MutableLiveData<WatchlistPeekDto>()
    val watchlist: LiveData<WatchlistPeekDto>
        get() = _watchlist

    private val _navigateToSelectedMovie = MutableLiveData<String?>()
    val navigateToSelectedMovie: LiveData<String?>
        get() = _navigateToSelectedMovie

    private val _deleteMovie = MutableLiveData<String?>()
    val deleteMovie: LiveData<String?>
        get() = _deleteMovie


    private fun getWatchlistFromDatabase(id: Long) {
        viewModelScope.launch {
            try {
                val watchlistDatabase = watchlistRepository.getWatchlist(id)
                _watchlist.value = watchlistDatabase
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun getWatchlist(id: Long) {
        getWatchlistFromDatabase(id)
    }

    private fun deleteWatchlistRequest(id: Long) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.deleteWatchlist(
                    SessionManager.mInstance.getToken()!!, id)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun deleteWatchlist(id: Long) {
        deleteWatchlistRequest(id)
    }

    private fun deleteMovieRequest(id: Long, movieId: String) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.deleteWatchlistMovie(
                    SessionManager.mInstance.getToken()!!, id, movieId)
                refreshWatchlist(id)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun deleteMovie(id: Long, movieId: String) {
        deleteMovieRequest(id, movieId)
    }

    private fun renameWatchlistRequest(id: Long, name: String) {
        viewModelScope.launch {
            try {
                CinemaLinkApi.retrofitService.renameWatchlist(
                    SessionManager.mInstance.getToken()!!, id, name)
                refreshWatchlist(id)
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun refreshWatchlist(id: Long) {
        viewModelScope.launch {
            try {
                watchlistRepository.refreshWatchlist(id)
                getWatchlist(id)
            } catch (e: HttpException) {
                Timber.e("${e.message}")
            }
        }
    }

    fun renameWatchlist(id: Long, name: String) {
        renameWatchlistRequest(id, name)
    }

    fun showMovie(movieId: String) {
        _navigateToSelectedMovie.value = movieId
    }

    fun showMovieComplete() {
        _navigateToSelectedMovie.value = null
    }

    fun deleteMovie(movieId: String) {
        _deleteMovie.value = movieId
    }

    fun deleteMovieComplete() {
        _deleteMovie.value = null
    }

}