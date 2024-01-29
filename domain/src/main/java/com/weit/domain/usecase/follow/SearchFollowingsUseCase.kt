package com.weit.domain.usecase.follow

import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.MayknowUserSearchInfo
import com.weit.domain.model.follow.SearchFollowRequestInfo
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class SearchFollowingsUseCase @Inject constructor(
    private val followRepository: FollowRepository,
) {
    suspend operator fun invoke(
        searchFollowRequestInfo: SearchFollowRequestInfo,
    ): Result<List<FollowUserContent>> = followRepository.getSearchFollowings(searchFollowRequestInfo)
}
