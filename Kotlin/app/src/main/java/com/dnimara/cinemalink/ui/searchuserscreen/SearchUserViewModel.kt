package com.dnimara.cinemalink.ui.searchuserscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import com.dnimara.cinemalink.utils.SessionManager
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchUserViewModel: ViewModel() {

    private val _searchUsers = MutableLiveData<List<UserDto>>()
    val users: LiveData<List<UserDto>>
        get() = _searchUsers

    private val _navigateToSelectedUserProfile = MutableLiveData<Long?>()
    val navigateToSelectedUserProfile: LiveData<Long?>
        get() = _navigateToSelectedUserProfile

    private fun searchUsersRequest(searchTerm: String) {
        viewModelScope.launch {
            try {
                val searchUsersResult = CinemaLinkApi.retrofitService.searchUsers(
                    SessionManager.mInstance.getToken()!!, searchTerm)
                _searchUsers.value = searchUsersResult
            } catch (e: Exception) {
                Timber.e("${e.message}")
            }
        }
    }

    fun search(searchTerm: String) {
        searchUsersRequest(searchTerm)
    }

    fun displayUserProfile(userId: Long) {
        _navigateToSelectedUserProfile.value = userId
    }

    fun displayUserProfileComplete() {
        _navigateToSelectedUserProfile.value = null
    }

}