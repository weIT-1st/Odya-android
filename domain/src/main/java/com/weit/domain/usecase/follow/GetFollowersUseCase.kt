package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class GetFollowersUseCase @Inject constructor(
    private val followRepository: FollowRepository,
) {
    suspend operator fun invoke(
        followerSearchInfo: FollowerSearchInfo,
    ): Result<List<FollowUserContent>> = followRepository.getFollowers(followerSearchInfo)
}
