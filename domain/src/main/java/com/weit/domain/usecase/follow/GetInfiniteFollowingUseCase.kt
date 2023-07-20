package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.FollowSearchDetail
import com.weit.domain.model.follow.FollowingSearchInfo
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class GetInfiniteFollowingUseCase @Inject constructor(
    private val followRepository: FollowRepository,
) {
    suspend operator fun invoke(followingSearchInfo: FollowingSearchInfo): Result<FollowSearchDetail> =
        followRepository.getInfiniteFollowing(followingSearchInfo)
}
