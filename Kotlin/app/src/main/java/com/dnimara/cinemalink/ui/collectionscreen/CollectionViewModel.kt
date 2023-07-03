package com.dnimara.cinemalink.ui.collectionscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import com.dnimara.cinemalink.utils.SessionManager
import kotlinx.coroutines.launch
import timber.log.Timber

class CollectionViewModel: ViewModel() {

    private val _collection = MutableLiveData<CollectionDto>()
    val collection: LiveData<CollectionDto>
        get() = _collection

    private val _navigateToSelectedMovie = MutableLiveData<String?>()
    val navigateToSelectedMovie: LiveData<String?>
        get() = _navigateToSelectedMovie

    private val _filterScroll = MutableLiveData<Boolean>(false)
    val filterScroll: LiveData<Boolean>
        get() = _filterScroll

    private fun getUserCollectionRequest() {
        viewModelScope.launch {
            try {
                val collectionResult = CinemaLinkApi.retrofitService.getUserCollection(
                    SessionManager.mInstance.getToken()!!)
                _collection.value = collectionResult
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun getUserCollection() {
        getUserCollectionRequest()
    }

    fun displayMovie(movieId: String) {
        _navigateToSelectedMovie.value = movieId
    }

    fun displayMovieComplete() {
        _navigateToSelectedMovie.value = null
    }

    fun filterScroll() {
        _filterScroll.value = true
    }

    fun filterScrollDone() {
        _filterScroll.value = false
    }

}