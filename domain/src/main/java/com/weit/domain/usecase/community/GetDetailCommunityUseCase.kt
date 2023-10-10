package com.weit.domain.usecase.community

import com.weit.domain.model.community.CommunityContent
import com.weit.domain.model.community.CommunityRegistrationInfo
import com.weit.domain.repository.community.comment.CommunityRepository
import javax.inject.Inject

class GetDetailCommunityUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(
        communityId : Long
    ): Result<CommunityContent> =
        communityRepository.getDetailCommunity(communityId)
}
