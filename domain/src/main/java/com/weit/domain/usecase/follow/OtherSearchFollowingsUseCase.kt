package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.MayknowUserSearchInfo
import com.weit.domain.model.follow.SearchFollowRequestInfo
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class OtherSearchFollowingsUseCase @Inject constructor(
    private val followRepository: FollowRepository,
) {
    suspend operator fun invoke(
        userId: Long,
        searchFollowRequestInfo: SearchFollowRequestInfo,
    ): Result<List<FollowUserContent>> =
        followRepository.getOtherSearchFollowings(userId, searchFollowRequestInfo)
}
