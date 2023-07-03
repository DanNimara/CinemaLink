package com.dnimara.cinemalink.ui.searchmoviescreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnimara.cinemalink.ui.moviescreen.MovieSummaryDto
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchMovieViewModel: ViewModel() {

    private val _searchMovies = MutableLiveData<List<MovieSummaryDto>>()
    val searchMovies: LiveData<List<MovieSummaryDto>>
        get() = _searchMovies

    private val _navigateToSelectedMovie = MutableLiveData<MovieSummaryDto?>()
    val navigateToSelectedMovie: LiveData<MovieSummaryDto?>
        get() = _navigateToSelectedMovie

    private fun searchMoviesRequest(searchTerm: String) {
        viewModelScope.launch {
            try {
                 val searchMoviesResult = CinemaLinkApi.retrofitService.searchMovies(
                    searchTerm)
                _searchMovies.value = searchMoviesResult
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }

    }

    fun search(searchTerm: String) {
        searchMoviesRequest(searchTerm)
    }

    fun displayMovie(movie: MovieSummaryDto) {
        _navigateToSelectedMovie.value = movie
    }

    fun displayMovieComplete() {
        _navigateToSelectedMovie.value = null
    }

}