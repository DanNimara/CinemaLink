package com.dnimara.cinemalink.ui.service

import com.dnimara.cinemalink.database.PostDto
import com.dnimara.cinemalink.ui.collectionscreen.CollectionDto
import com.dnimara.cinemalink.ui.feedscreen.*
import com.dnimara.cinemalink.ui.loginscreen.LoginRequest
import com.dnimara.cinemalink.ui.loginscreen.TokenDto
import com.dnimara.cinemalink.ui.moviescreen.*
import com.dnimara.cinemalink.ui.profilescreen.ProfileDto
import com.dnimara.cinemalink.ui.recommendationscreen.RecommendationDto
import com.dnimara.cinemalink.ui.reviewscreen.AddReviewDto
import com.dnimara.cinemalink.ui.reviewscreen.ReviewCommentsDto
import com.dnimara.cinemalink.ui.reviewscreen.ReviewDto
import com.dnimara.cinemalink.ui.searchuserscreen.UserDto
import com.dnimara.cinemalink.ui.watchlistscreens.WatchlistItemDto
import com.dnimara.cinemalink.ui.watchlistscreens.WatchlistPeekDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import retrofit2.http.Query as Query
// emulator
//private const val BASE_URL = "http://10.0.2.2:8001"
// tg jiu
//private const val BASE_URL = "http://192.168.0.104:8001"
// camera camin
//private const val BASE_URL = "http://192.168.137.192:8001"
// softbinator
//private const val BASE_URL = "http://192.168.1.71:8001"
// camera toni
//private const val BASE_URL = "http://192.168.1.247:8001"
// facultate
//private const val BASE_URL = "http://10.11.172.52:8001"
// rares
//private const val BASE_URL = "http://192.168.43.6:8001"
// denis
// private const val BASE_URL = "http://192.168.100.16:8001"
// lumiere
private const val BASE_URL = "http://192.168.100.193:8001"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface CinemaLinkApiService {

    @POST("/auth")
    suspend fun login(@Body loginRequest: LoginRequest): TokenDto

    @Multipart
    @POST("/users/register")
    suspend fun register(@Part(value = "email") email: RequestBody,
                         @Part(value = "username") username: RequestBody,
                         @Part(value = "password") password: RequestBody,
                         @Part profilePicture: MultipartBody.Part?)

    @GET("/users/profile/{id}")
    suspend fun getUserProfile(@Header("Authorization") accessToken: String,
                               @Path("id") id: Long): ProfileDto

    @POST("/users/follow/{id}")
    suspend fun followUser(@Header("Authorization") accessToken: String,
                            @Path("id") id: Long)

    @Multipart
    @PATCH("/users/update-profile-pic")
    suspend fun updateProfilePic(@Header("Authorization") accessToken: String,
                                @Part profilePicture: MultipartBody.Part)

    @PATCH("/users/update-username")
    suspend fun updateUsername(@Header("Authorization") accessToken: String,
                                 @Query("username") username: String)

    @POST("/users/search")
    suspend fun searchUsers(@Header("Authorization") accessToken: String,
                            @Query("searchTerm") searchTerm: String): List<UserDto>

    @GET("/posts")
    suspend fun getFeed(@Header("Authorization") accessToken: String): List<PostDto>

    @GET("/posts/{id}")
    suspend fun getPost(@Header("Authorization") accessToken: String,
                        @Path("id") id: Long): PostCommentsDto

    @POST("/posts/{id}/comment")
    suspend fun addComment(@Header("Authorization") accessToken: String,
                           @Path("id") id: Long,
                           @Body editDto: EditDto)

    @POST("/posts")
    suspend fun createPost(@Header("Authorization") accessToken: String, @Body createPost: CreatePostRequest)

    @PATCH("/posts/edit/{id}")
    suspend fun editPost(@Header("Authorization") accessToken: String,
                            @Path("id") id: Long,
                            @Body editDto: EditDto)

    @DELETE("/posts/delete/{id}")
    suspend fun deletePost(@Header("Authorization") accessToken: String, @Path("id") id: Long)

    @PATCH("/posts/reaction")
    suspend fun reactToPost(@Header("Authorization") accessToken: String, @Body likeDto: LikeDto)

    @PATCH("/comments/reaction")
    suspend fun reactToComment(@Header("Authorization") accessToken: String, @Body likeDto: LikeDto)

    @PATCH("/comments/edit/{id}")
    suspend fun editComment(@Header("Authorization") accessToken: String,
                               @Path("id") id: Long,
                               @Body editDto: EditDto)

    @DELETE("/comments/delete/{id}")
    suspend fun deleteComment(@Header("Authorization") accessToken: String, @Path("id") id: Long)

    @GET("/movies/{id}")
    suspend fun getMovieById(@Header("Authorization") accessToken: String,
        @Path("id") id: String): MovieDetailsDto

    @POST("/movies/search")
    suspend fun searchMovies(@Query("searchTerm") searchTerm: String): List<MovieSummaryDto>

    @GET("/movies/recommend")
    suspend fun getUserMovieRecommendations(@Header("Authorization") accessToken: String):
            List<RecommendationDto>

    @POST("/movies/tag-search")
    suspend fun searchMoviesTag(@Query("searchTerm") searchTerm: String): List<MovieTagDto>

    @POST("/movies/rate")
    suspend fun rateMovie(@Header("Authorization") accessToken: String,
                          @Body rateMovieDto: RateMovieDto
    )

    @POST("/movies/unrate")
    suspend fun unrateMovie(@Header("Authorization") accessToken: String,
                          @Query("movieId") movieId: String)

    @POST("/movies/report-availability")
    suspend fun reportAvailability(@Header("Authorization") accessToken: String,
                                   @Body availabilityReportDto: AvailabilityReportDto)

    @POST("/collections/add-movie")
    suspend fun updateMovieForCollection(@Header("Authorization") accessToken: String,
                                   @Body addMovieToCollectionDto: AddMovieToCollectionDto)

    @GET("/collections")
    suspend fun getUserCollection(@Header("Authorization") accessToken: String): CollectionDto

    @GET("/watchlists")
    suspend fun getUserWatchlists(@Header("Authorization") accessToken: String): List<WatchlistPeekDto>

    @GET("/watchlists/{id}")
    suspend fun getWatchlist(@Header("Authorization") accessToken: String,
                            @Path("id") id: Long): WatchlistPeekDto

    @POST("/watchlists")
    suspend fun createWatchlist(@Header("Authorization") accessToken: String,
                                @Query("name") name: String)

    @GET("/watchlists/choose")
    suspend fun getUserWatchlistsForChoice(@Header("Authorization") accessToken: String): List<WatchlistItemDto>

    @PATCH("/watchlists")
    suspend fun addMovieToWatchlist(@Header("Authorization") accessToken: String,
                                    @Body addMovieToWatchlistDto: AddMovieToWatchlistDto)

    @PATCH("/watchlists/rename/{id}")
    suspend fun renameWatchlist(@Header("Authorization") accessToken: String,
                                @Path("id") id: Long,
                                @Query("newName") newName: String)

    @DELETE("/watchlists/{id}")
    suspend fun deleteWatchlist(@Header("Authorization") accessToken: String,
                                @Path("id") id: Long)

    @DELETE("/watchlists/{id}/delete-movie")
    suspend fun deleteWatchlistMovie(@Header("Authorization") accessToken: String,
                                @Path("id") id: Long,
                                @Query("movieId") movieId: String)

    @GET("/reviews/movie/{id}")
    suspend fun getUserReviews(@Header("Authorization") accessToken: String,
                               @Path("id") id: String): List<ReviewDto>

    @GET("/reviews/{id}")
    suspend fun getReview(@Header("Authorization") accessToken: String,
                               @Path("id") id: Long): ReviewCommentsDto

    @GET("/reviews/muser-review/{id}")
    suspend fun getUserReview(@Header("Authorization") accessToken: String,
                               @Path("id") id: String): ReviewDto

    @POST("/reviews")
    suspend fun addReview(@Header("Authorization") accessToken: String,
                          @Body addReviewDto: AddReviewDto)

    @PATCH("/reviews/edit/{id}")
    suspend fun editReview(@Header("Authorization") accessToken: String,
                         @Path("id") id: Long,
                         @Body editDto: EditDto)

    @DELETE("/reviews/delete/{id}")
    suspend fun deleteReview(@Header("Authorization") accessToken: String,
                             @Path("id") id: Long)

    @PATCH("/reviews/reaction")
    suspend fun reactToReview(@Header("Authorization") accessToken: String,
                              @Body likeDto: LikeDto)

    @POST("/reviews/{id}/comment")
    suspend fun addReviewComment(@Header("Authorization") accessToken: String,
                           @Path("id") id: Long,
                           @Body editDto: EditDto)

    @PATCH("/review-comments/reaction")
    suspend fun reactToReviewComment(@Header("Authorization") accessToken: String, @Body likeDto: LikeDto)

    @PATCH("/review-comments/edit/{id}")
    suspend fun editReviewComment(@Header("Authorization") accessToken: String,
                                  @Path("id") id: Long,
                                  @Body editDto: EditDto)

    @PATCH("/review-comments/delete/{id}")
    suspend fun deleteReviewComment(@Header("Authorization") accessToken: String, @Path("id") id: Long)

}

object CinemaLinkApi {
    val retrofitService : CinemaLinkApiService by lazy {
        retrofit.create(CinemaLinkApiService::class.java)
    }
}