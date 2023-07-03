package com.dnimara.cinemalink.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PostDao {

    @Transaction
    @Query("select * from posts p WHERE p.deleted = 0 ORDER BY p.postId DESC")
    fun getPostsWithMovies(): LiveData<List<PostWithMovies>>

    @Transaction
    @Query("select * from posts order by postId DESC")
    fun getPosts(): LiveData<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<Post>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(postMovieCrossRef: PostMovieCrossRef)

    @Insert
    fun insert(post: Post)

    @Update
    fun update(post: Post)

    @Transaction
    @Query("SELECT * from posts p WHERE p.postId = :key")
    fun get(key: Long): PostWithMovies?

    @Query("DELETE FROM posts")
    fun clear()

    @Query("DELETE FROM post_movie_join")
    fun clearPostsMovies()

}