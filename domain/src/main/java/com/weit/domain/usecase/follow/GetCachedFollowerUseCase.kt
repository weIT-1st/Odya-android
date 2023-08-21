package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class GetCachedFollowerUseCase @Inject constructor(
    private val repository: FollowRepository,
) {
    operator fun invoke(query: String): List<FollowUserContent> =
        repository.getCachedFollower(query)
}
