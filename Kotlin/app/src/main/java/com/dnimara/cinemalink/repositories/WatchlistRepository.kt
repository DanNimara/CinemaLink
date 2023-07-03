package com.dnimara.cinemalink.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.database.*
import com.dnimara.cinemalink.domain.TagMovie
import com.dnimara.cinemalink.ui.moviescreen.MovieSummaryDto
import com.dnimara.cinemalink.ui.moviescreen.asDatabaseModel
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import com.dnimara.cinemalink.ui.watchlistscreens.WatchlistDto
import com.dnimara.cinemalink.ui.watchlistscreens.WatchlistPeekDto
import com.dnimara.cinemalink.ui.watchlistscreens.asDatabaseModel
import com.dnimara.cinemalink.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WatchlistRepository(private val database: CinemaLinkDatabase) {

    val watchlists: LiveData<List<WatchlistDto>> = Transformations.map(
        database.watchlistDao.getWatchlists()) {
        it.asDomainModdel()
    }

    suspend fun refreshWatchlists() {
        withContext(Dispatchers.IO) {
            val watchlistsResponse = CinemaLinkApi.retrofitService
                .getUserWatchlists(SessionManager.mInstance.getToken()!!)
            database.watchlistDao.clear()
            database.watchlistDao.clearWatchlistMovies()
            database.watchlistDao.insertAll(watchlistsResponse.asDatabaseModel())

            for (watchlist in watchlistsResponse) {
                if (watchlist.movies != null) {
                    database.movieDao.insertAll(watchlist.movies.asDatabaseModel())
                    for (movie in watchlist.movies) {
                        database.watchlistDao.insert(WatchlistMovieCrossRef(watchlist.id, movie.id, movie.note, movie.addedTime))
                    }
                }

            }
        }
    }

    suspend fun refreshWatchlist(id: Long) {
        withContext(Dispatchers.IO) {
            val watchlistResponse = CinemaLinkApi.retrofitService
                .getWatchlist(SessionManager.mInstance.getToken()!!, id)
            database.watchlistDao.clearWatchlist(id)
            database.watchlistDao.clearWatchlistMoviesOfWatchlist(id)
            database.watchlistDao.insert(Watchlist(watchlistResponse.id, watchlistResponse.name,
                                watchlistResponse.createdTime, watchlistResponse.watchlistStatus,
                                watchlistResponse.movieCount))
            if (watchlistResponse.movies != null) {
                database.movieDao.insertAll(watchlistResponse.movies.asDatabaseModel())
                for (movie in watchlistResponse.movies) {
                    database.watchlistDao.insert(WatchlistMovieCrossRef(watchlistResponse.id, movie.id, movie.note, movie.addedTime))
                }
            }
        }
    }

    suspend fun getWatchlist(id: Long): WatchlistPeekDto {
        return withContext(Dispatchers.IO) {
            val watchlist = database.watchlistDao.getWatchlistWithMovies(id)
            return@withContext WatchlistPeekDto(
                watchlist.watchlist.watchlistId,
                watchlist.watchlist.name, watchlist.watchlist.createdTime,
                watchlist.watchlist.watchlistStatus, watchlist.watchlist.movieCount,
                watchlist.movies.map { mov ->
                    MovieSummaryDto(
                        mov.movieId, mov.title, mov.posterUrl,
                        mov.outline, mov.releaseDay, mov.releaseMonth, mov.releaseYear,
                        mov.duration, mov.internalRating, mov.imdbRating,
                        database.watchlistDao.getNote(id, mov.movieId),
                        null, null
                    )
                }
            )
        }
    }

}