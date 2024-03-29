package com.weit.data.source

import com.weit.data.model.topic.FavoriteTopicDTO
import com.weit.data.model.topic.TopicDTO
import com.weit.data.model.topic.TopicRegistration
import com.weit.data.service.TopicService
import retrofit2.Response
import javax.inject.Inject

class TopicDataSource @Inject constructor(
    private val service: TopicService,
) {

    suspend fun registerFavoriteTopic(topicRegistration: TopicRegistration) {
        service.registerFavoriteTopic(topicRegistration)
    }

    suspend fun deleteFavoriteTopic(topicId: Long): Response<Unit> {
        return service.deleteFavoriteTopic(topicId)
    }

    suspend fun getTopicList(): List<TopicDTO> =
        service.getTopicList()

    suspend fun getFavoriteTopicList(): List<FavoriteTopicDTO> =
        service.getFavoriteTopicList()
}
