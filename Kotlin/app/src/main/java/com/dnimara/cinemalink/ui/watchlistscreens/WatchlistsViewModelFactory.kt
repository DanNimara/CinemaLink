package com.dnimara.cinemalink.ui.watchlistscreens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.database.CinemaLinkDatabase
import java.lang.IllegalArgumentException

class WatchlistsViewModelFactory(
    private val dataSource: CinemaLinkDatabase,
    private val application: CinemaLinkApplication): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WatchlistsViewModel::class.java)) {
            return WatchlistsViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}