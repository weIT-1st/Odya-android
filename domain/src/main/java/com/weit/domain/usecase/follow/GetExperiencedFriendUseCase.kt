package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.ExperiencedFriendInfo
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class GetExperiencedFriendUseCase @Inject constructor(
    private val repository: FollowRepository,
) {
    suspend operator fun invoke(placeId: String): Result<ExperiencedFriendInfo> =
        repository.getExperiencedFriend(placeId)
}
