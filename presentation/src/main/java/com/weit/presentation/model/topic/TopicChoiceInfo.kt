package com.weit.presentation.model.topic

data class TopicChoiceInfo(
    val topicId: Long,
    val topicWord: String,
    val isChoice: Boolean = false
)
