package com.weit.data.service

import com.weit.data.model.topic.FavoriteTopicDTO
import com.weit.data.model.topic.TopicDTO
import com.weit.data.model.topic.TopicRegistration
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TopicService {

    @POST("/api/v1/topics")
    suspend fun registerFavoriteTopic(
        @Body topicRegistration: TopicRegistration,
    )

    @DELETE("/api/v1/topics/{id}")
    suspend fun deleteFavoriteTopic(
        @Path("id") id: Long,
    ): Response<Unit>

    @GET("/api/v1/topics")
    suspend fun getTopicList(): List<TopicDTO>

    @GET("/api/v1/topics/favorite")
    suspend fun getFavoriteTopicList(): List<FavoriteTopicDTO>
}
