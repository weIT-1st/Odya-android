package com.weit.domain.repository.topic

import com.weit.domain.model.topic.TopicDetail

interface TopicRepository {
    suspend fun registerFavoriteTopic(
        topicIdList: List<Long>,
    ): Result<Unit>

    suspend fun deleteFavoriteTopic(
        topicId: Long,
    ): Result<Unit>

    suspend fun getTopicList(): Result<List<TopicDetail>>

    suspend fun getFavoriteTopicList(): Result<List<TopicDetail>>
}
