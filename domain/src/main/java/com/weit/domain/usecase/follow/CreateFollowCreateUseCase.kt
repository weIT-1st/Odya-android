package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class CreateFollowCreateUseCase @Inject constructor(
    private val followRepository: FollowRepository
) {
    suspend fun invoke(followFollowingIdInfo: FollowFollowingIdInfo): Result<Unit> =
        followRepository.createFollow(followFollowingIdInfo)
}