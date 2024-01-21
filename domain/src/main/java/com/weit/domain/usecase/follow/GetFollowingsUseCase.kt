package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.FollowingSearchInfo
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class GetFollowingsUseCase @Inject constructor(
    private val followRepository: FollowRepository,
) {
    suspend operator fun invoke(
        followingSearchInfo: FollowingSearchInfo,
    ): Result<List<FollowUserContent>> = followRepository.getFollowings(followingSearchInfo)
}
