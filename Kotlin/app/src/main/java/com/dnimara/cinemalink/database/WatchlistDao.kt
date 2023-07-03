package com.dnimara.cinemalink.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WatchlistDao {

    @Query("select m.* from watchlist_movie_join wm join " +
            "movies m on (wm.movieId = m.movieId) where wm.watchlistId = :id order by wm.addedTime DESC")
    fun getMoviesForWatchlist(id: Long): List<Movie>

    @Transaction
    @Query("select wm.note from watchlist_movie_join wm where wm.watchlistId = :watchlistId " +
            "and wm.movieId = :movieId")
    fun getNote(watchlistId: Long, movieId: String): String?

    @Transaction
    @Query("select * from watchlists")
    fun getWatchlists(): LiveData<List<Watchlist>>

    @Query("select * from watchlists w where w.watchlistId = :id")
    fun getWatchlist(id: Long): Watchlist

    @Transaction
    fun getWatchlistWithMovies(id: Long): WatchlistWithMovies {
        return WatchlistWithMovies(getWatchlist(id), getMoviesForWatchlist(id))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(watchlists: List<Watchlist>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(watchlistMovieCrossRef: WatchlistMovieCrossRef)

    @Insert
    fun insert(watchlist: Watchlist)

    @Query("delete from watchlists")
    fun clear()

    @Query("delete from watchlists where watchlistId = :id")
    fun clearWatchlist(id: Long)

    @Query("delete from watchlist_movie_join")
    fun clearWatchlistMovies()

    @Query("delete from watchlist_movie_join where watchlistId = :id")
    fun clearWatchlistMoviesOfWatchlist(id: Long)

}