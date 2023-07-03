package com.dnimara.cinemalink.ui.loginscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class LoginViewModel: ViewModel() {

    private val _token = MutableLiveData<TokenDto>()
    val token: LiveData<TokenDto>
        get() = _token

    private val _loggedIn = MutableLiveData<Boolean>()
    val loggedIn: LiveData<Boolean>
        get() = _loggedIn

    private val _eventNetworkError = MutableLiveData<String?>()
    val eventNetworkError: LiveData<String?>
        get() = _eventNetworkError

    private fun loginRequest(username: String, password: String) {
        viewModelScope.launch {
            try {

                val tokenResult = CinemaLinkApi.retrofitService.login(
                    LoginRequest(username,
                    password)
                )
                _token.value = tokenResult
                onLoggedIn()
            } catch (e: HttpException) {
                showError(e.response()?.errorBody()?.string()!!)
            } catch (e: Exception) {
                showError(e.message.toString())
            }
        }
    }

    fun login(username: String, password: String) {
        loginRequest(username, password)
    }

    fun onLoggedIn() {
        _loggedIn.value = true
    }
    fun onNotLoggedIn() {
        _loggedIn.value = false
    }

    fun showError(message: String) {
        _eventNetworkError.value = message
    }

    fun showErrorDone() {
        _eventNetworkError.value = null
    }

}