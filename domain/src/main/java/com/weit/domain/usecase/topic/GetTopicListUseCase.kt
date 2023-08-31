package com.weit.domain.usecase.topic

import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.repository.topic.TopicRepository
import javax.inject.Inject

class GetTopicListUseCase @Inject constructor(
    private val repository: TopicRepository,
) {
    suspend operator fun invoke(): Result<List<TopicDetail>> =
        repository.getTopicList()
}
