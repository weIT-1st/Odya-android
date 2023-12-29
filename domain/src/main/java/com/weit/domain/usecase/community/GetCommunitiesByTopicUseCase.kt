package com.weit.domain.usecase.community

import com.weit.domain.model.community.CommunityMainContent
import com.weit.domain.model.community.CommunityRegistrationInfo
import com.weit.domain.model.community.CommunityRequestInfo
import com.weit.domain.repository.community.comment.CommunityRepository
import javax.inject.Inject

class GetCommunitiesByTopicUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(
        topicId: Long,
        communityRequestInfo: CommunityRequestInfo,
    ): Result<List<CommunityMainContent>> =
        communityRepository.getCommunitiesByTopic(topicId,communityRequestInfo)
}
