package com.weit.domain.usecase.topic

import com.weit.domain.repository.favoritePlace.FavoritePlaceRepository
import com.weit.domain.repository.topic.TopicRepository
import javax.inject.Inject

class RegisterFavoriteTopicUseCase @Inject constructor(
    private val repository: TopicRepository,
) {
    suspend operator fun invoke(topicIdList: List<Long>): Result<Unit> =
        repository.registerFavoriteTopic(topicIdList)
}
