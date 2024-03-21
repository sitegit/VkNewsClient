package com.example.vknewsclient.data.network

import com.example.vknewsclient.data.model.fave.FaveResponseDto
import com.example.vknewsclient.data.model.general.CommentsResponseDto
import com.example.vknewsclient.data.model.general.LikesCountResponseDto
import com.example.vknewsclient.data.model.recomendation.NewsFeedResponseDto
import com.example.vknewsclient.data.model.user.UserResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("newsfeed.getRecommended?v=$VERSION_API")
    suspend fun loadRecommendations(
        @Query("access_token") token: String
    ): NewsFeedResponseDto

    @GET("newsfeed.getRecommended?v=$VERSION_API")
    suspend fun loadRecommendations(
        @Query("access_token") token: String,
        @Query("start_from") startFrom: String
    ): NewsFeedResponseDto

    @GET("likes.add?v=$VERSION_API&type=post")
    suspend fun addLike(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("item_id") postId: Long
    ): LikesCountResponseDto

    @GET("likes.delete?v=$VERSION_API&type=post")
    suspend fun deleteLike(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("item_id") postId: Long
    ): LikesCountResponseDto

    @GET("fave.addPost?v=$VERSION_API")suspend fun addFave(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("id") postId: Long
    )
    @GET("fave.removePost?v=$VERSION_API")
    suspend fun removeFave(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("id") postId: Long
    )
    @GET("fave.get?v=$VERSION_API&item_type=post&extended=1")
    suspend fun getFave(
        @Query("access_token") token: String
    ): FaveResponseDto

    @GET("newsfeed.ignoreItem?v=$VERSION_API&type=wall")
    suspend fun ignorePost(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("item_id") postId: Long
    )

    @GET("wall.getComments?v=$VERSION_API&extended=1&fields=photo_100")
    suspend fun getComments(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("post_id") postId: Long
    ): CommentsResponseDto

    @GET("users.get?v=$VERSION_API")
    suspend fun getUser(
        @Query("access_token") accessToken: String,
        @Query("fields") fields: String = "photo_200,city,contacts,counters"
    ): UserResponseDto

    companion object {
        private const val VERSION_API = "5.199"
    }
}