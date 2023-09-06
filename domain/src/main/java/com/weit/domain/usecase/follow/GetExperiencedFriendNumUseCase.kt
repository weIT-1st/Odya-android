package com.weit.domain.usecase.follow

import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class GetExperiencedFriendNumUseCase @Inject constructor(
    private val repository: FollowRepository
){
    suspend operator fun invoke(placeId: String): Result<Int> =
        repository.getExperiencedFriendNum(placeId)
}
