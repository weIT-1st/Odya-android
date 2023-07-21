package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class DeleteFollowUseCase @Inject constructor(
    private val followRepository: FollowRepository,
) {
    suspend operator fun invoke(followFollowingIdInfo: FollowFollowingIdInfo): Result<Unit> =
        followRepository.deleteFollow(followFollowingIdInfo)
}
