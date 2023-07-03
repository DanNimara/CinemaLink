package com.dnimara.cinemalink.ui.registerscreen

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnimara.cinemalink.ui.loginscreen.LoginRequest
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.createFormData
import okhttp3.RequestBody
import retrofit2.HttpException
import timber.log.Timber
import java.io.File

class RegisterViewModel: ViewModel() {

    private val _redirectToLoginScreen = MutableLiveData<Boolean>()
    val redirectToLoginScreen: LiveData<Boolean>
        get() = _redirectToLoginScreen

    private val _eventNetworkError = MutableLiveData<String?>()
    val eventNetworkError: LiveData<String?>
        get() = _eventNetworkError

    init {
        _redirectToLoginScreen.value = false
    }

    private fun registerRequest(email: String, username: String, password: String,
                             profilePicture: File?) {
        viewModelScope.launch {
            try {
                var filePart: MultipartBody.Part? = null
                if (profilePicture != null) {
                    filePart = createFormData(
                        "profilePicture", profilePicture.name, RequestBody.create(
                            MediaType.parse("image"), profilePicture
                        )
                    )
                }
                val emailPart = RequestBody.create(MediaType.parse("multipart/form-data"), email)
                val usernamePart = RequestBody.create(MediaType.parse("multipart/form-data"), username)
                val passwordPart = RequestBody.create(MediaType.parse("multipart/form-data"), password)

                CinemaLinkApi.retrofitService.register( emailPart,
                    usernamePart, passwordPart, filePart)
                redirectToLoginScreen()
            } catch (e: HttpException) {
                showError(e.response()?.errorBody()?.string()!!)
            } catch (e: Exception) {
                showError(e.message.toString())
            }
        }
    }

    fun redirectToLoginScreen() {
        _redirectToLoginScreen.value = true
    }

    fun redirectToLoginScreenComplete() {
        _redirectToLoginScreen.value = false
    }

    fun register(email: String, username: String, password: String,
        profilePicture: File?) {
        registerRequest(email, username, password, profilePicture)
    }

    fun showError(message: String) {
        _eventNetworkError.value = message
    }

    fun showErrorDone() {
        _eventNetworkError.value = null
    }

}