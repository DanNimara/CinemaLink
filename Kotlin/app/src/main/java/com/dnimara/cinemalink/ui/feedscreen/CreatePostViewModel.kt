package com.dnimara.cinemalink.ui.feedscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import kotlinx.coroutines.launch
import timber.log.Timber

class CreatePostViewModel: ViewModel() {

    private val _getFeedData = MutableLiveData<Boolean>()
    val getFeedData: LiveData<Boolean>
        get() = _getFeedData

    private val _showToastTagLimit = MutableLiveData<Boolean>()
    val showToastTagLimit: LiveData<Boolean>
        get() = _showToastTagLimit

    private val _searchMovies = MutableLiveData<List<MovieTagDto>>()
    val searchMovies: LiveData<List<MovieTagDto>>
        get() = _searchMovies

    private val _tagMovies = MutableLiveData<List<MovieTagDto>>()
    val tagMovies: LiveData<List<MovieTagDto>>
        get() = _tagMovies

    init {
        _getFeedData.value = false
    }

    private fun createPostRegister(accessToken: String, content: String) {
        viewModelScope.launch {
            try {
                val tagList: MutableList<String> = mutableListOf()
                if (_tagMovies.value?.size != null && _tagMovies.value?.size != 0) {
                    _tagMovies.value!!.map {
                        tagList.add(it.id)
                    }
                }
                CinemaLinkApi.retrofitService
                    .createPost(accessToken, CreatePostRequest(content, tagList))
                getFeedData()
                Timber.i("YEY")
            } catch(e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun getFeedData() {
        _getFeedData.value = true
    }

    fun getFeedDataComplete() {
        _getFeedData.value = false
    }

    fun createPost(accessToken: String, content: String) {
        createPostRegister(accessToken, content)
    }

    private fun searchMoviesRequest(searchTerm: String) {
        viewModelScope.launch {
            try {
                val searchMoviesResult = CinemaLinkApi.retrofitService.searchMoviesTag(
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

    fun addTag(movieTag: MovieTagDto) {
        if (_tagMovies.value?.size == null || (_tagMovies.value?.contains(movieTag) == false
            && _tagMovies.value?.size!! < 4)) {
            _tagMovies.value = _tagMovies.value?.plus(movieTag) ?: listOf(movieTag)
        } else if (_tagMovies.value?.size!! == 4) {
            _showToastTagLimit.value = true
        }
    }

    fun removeTags(length: Int): Int {
        var movieTitlesLength = _tagMovies.value?.map {movie -> movie.title}?.joinToString(separator = "")?.length
        while (movieTitlesLength!! > length) {
            _tagMovies.postValue(_tagMovies.value?.dropLast(1))
            movieTitlesLength = _tagMovies.value?.map {movie -> movie.title}?.joinToString(separator = "")?.length
        }
        return movieTitlesLength
    }

    fun showToastTagLimitComplete() {
        _showToastTagLimit.value = false
    }

}