package com.weit.domain.usecase.topic

import com.weit.domain.repository.favoritePlace.FavoritePlaceRepository
import com.weit.domain.repository.topic.TopicRepository
import javax.inject.Inject

class DeleteFavoriteTopicUseCase @Inject constructor(
    private val repository: TopicRepository,
) {
    suspend operator fun invoke(topicId: Long): Result<Unit> =
        repository.deleteFavoriteTopic(topicId)
}
