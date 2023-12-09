package com.weit.domain.usecase.community

import com.weit.domain.model.community.CommunityRegistrationInfo
import com.weit.domain.model.community.CommunityUpdateInfo
import com.weit.domain.repository.community.comment.CommunityRepository
import javax.inject.Inject

class UpdateCommunityUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(
        communityId: Long,
        communityUpdateInfo: CommunityUpdateInfo,
        communityImages: List<String>
    ): Result<Unit> =
        communityRepository.updateCommunity(communityId,communityUpdateInfo,communityImages)
}
