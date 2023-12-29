package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.topic.TopicDetail

@JsonClass(generateAdapter = true)
data class CommunityTopicDTO(
    @field:Json(name = "id") override val topicId: Long,
    @field:Json(name = "topic") override val topicWord: String,
) : TopicDetail
