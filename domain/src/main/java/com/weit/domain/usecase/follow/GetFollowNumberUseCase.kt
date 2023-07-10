package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.FollowNumDetail
import com.weit.domain.model.follow.FollowUserIdInfo
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class GetFollowNumberUseCase @Inject constructor(
    private val followRepository: FollowRepository
) {
    suspend operator fun invoke(followUserIdInfo: FollowUserIdInfo) : Result<FollowNumDetail> =
        followRepository.getFollowNumber(followUserIdInfo)
}