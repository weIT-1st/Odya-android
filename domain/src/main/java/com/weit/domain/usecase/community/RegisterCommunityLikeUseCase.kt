package com.weit.domain.usecase.community

import com.weit.domain.model.community.CommunityRegistrationInfo
import com.weit.domain.repository.community.comment.CommunityRepository
import javax.inject.Inject

class RegisterCommunityLikeUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(
        communityId: Long,
    ): Result<Unit> =
        communityRepository.registerCommunityLike(communityId)
}
