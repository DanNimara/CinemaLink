package com.dnimara.cinemalink.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.dnimara.cinemalink.database.CinemaLinkDatabase
import com.dnimara.cinemalink.database.PostMovieCrossRef
import com.dnimara.cinemalink.database.asDatabaseModel
import com.dnimara.cinemalink.database.asDomainModel
import com.dnimara.cinemalink.domain.FeedPost
import com.dnimara.cinemalink.ui.moviescreen.asDatabaseModel
import com.dnimara.cinemalink.ui.service.CinemaLinkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedRepository(private val database: CinemaLinkDatabase) {

    val posts: LiveData<List<FeedPost>> = Transformations.map(
        database.postDao.getPostsWithMovies()) {
        it.asDomainModel()
    }

    suspend fun refreshFeed(accessToken: String) {
        withContext(Dispatchers.IO) {
            val feed = CinemaLinkApi.retrofitService
                .getFeed(accessToken)
            database.postDao.insertAll(feed.asDatabaseModel())

            for (post in feed) {
                if (post.tags != null) {
                    database.movieDao.insertAll(post.tags.asDatabaseModel())
                    for (tag in post.tags) {
                        database.postDao.insert(PostMovieCrossRef(post.postId, tag.id))
                    }
                }
            }
        }
    }

}