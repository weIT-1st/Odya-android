package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class DeleteFollowerUseCase @Inject constructor(
    private val followRepository: FollowRepository,
) {
    suspend operator fun invoke(followerId: Long): Result<Unit> =
        followRepository.deleteFollower(followerId)
}
