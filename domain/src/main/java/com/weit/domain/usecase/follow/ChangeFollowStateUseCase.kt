package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.FollowFollowingIdInfo
import javax.inject.Inject

class ChangeFollowStateUseCase @Inject constructor(
    private val createFollowCreateUseCase: CreateFollowCreateUseCase,
    private val deleteFollowUseCase: DeleteFollowUseCase,
) {
    suspend operator fun invoke(userId: Long, followState: Boolean): Result<Unit> {
        return if (followState) {
            createFollowCreateUseCase(FollowFollowingIdInfo(userId))
        } else {
            deleteFollowUseCase(FollowFollowingIdInfo(userId))
        }
    }
}
