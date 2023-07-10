package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.FollowSearchDetail
import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class GetInfiniteFollowerUseCase @Inject constructor(
    private val followRepository: FollowRepository
) {
    suspend operator fun invoke(followerSearchInfo: FollowerSearchInfo): Result<FollowSearchDetail> =
        followRepository.getInfiniteFollower(followerSearchInfo)
}