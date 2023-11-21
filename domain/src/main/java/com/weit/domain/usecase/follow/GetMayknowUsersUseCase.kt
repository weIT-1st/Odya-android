package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.MayknowUserSearchInfo
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class GetMayknowUsersUseCase @Inject constructor(
    private val followRepository: FollowRepository,
) {
    suspend operator fun invoke(
        mayknowUserSearchInfo: MayknowUserSearchInfo,
    ): Result<List<FollowUserContent>> = followRepository.getMayknowUsers(mayknowUserSearchInfo)
}
