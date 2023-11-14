package com.weit.domain.usecase.community

import com.weit.domain.model.community.CommunityMainContent
import com.weit.domain.model.community.CommunityMyActivityCommentContent
import com.weit.domain.model.community.CommunityMyActivityContent
import com.weit.domain.model.community.CommunityRegistrationInfo
import com.weit.domain.model.community.CommunityRequestInfo
import com.weit.domain.repository.community.comment.CommunityRepository
import javax.inject.Inject

class GetMyCommentCommunitiesUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(
        communityRequestInfo: CommunityRequestInfo,
    ): Result<List<CommunityMyActivityCommentContent>> =
        communityRepository.getMyCommentCommunities(communityRequestInfo)
}
