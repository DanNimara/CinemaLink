package com.dnimara.cinemalink.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Post::class, Movie::class, PostMovieCrossRef::class,
                     Watchlist::class, WatchlistMovieCrossRef::class], version = 13)
abstract class CinemaLinkDatabase: RoomDatabase() {

    abstract val postDao: PostDao
    abstract val movieDao: MovieDao
    abstract val watchlistDao: WatchlistDao

    companion object {

        private lateinit var INSTANCE: CinemaLinkDatabase

        fun getDatabase(context: Context): CinemaLinkDatabase {
            synchronized(CinemaLinkDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        CinemaLinkDatabase::class.java, "cinemalink")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }

    }
}