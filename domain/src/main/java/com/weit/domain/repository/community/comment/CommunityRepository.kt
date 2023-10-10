package com.weit.domain.repository.community.comment

import com.weit.domain.model.community.CommunityContent
import com.weit.domain.model.community.CommunityRequestInfo
import com.weit.domain.model.community.CommunityMainContent
import com.weit.domain.model.community.CommunityRegistrationInfo
import com.weit.domain.model.community.CommunityUpdateInfo

interface CommunityRepository {
    suspend fun registerCommunity(
        communityRegistrationInfo: CommunityRegistrationInfo,
        communityImages: List<String>
    ): Result<Unit>

    suspend fun getCommunities(communityRequestInfo: CommunityRequestInfo): Result<List<CommunityMainContent>>

    suspend fun getDetailCommunity(communityId: Long): Result<CommunityContent>

    suspend fun getMyCommunities(communityRequestInfo: CommunityRequestInfo): Result<List<CommunityMainContent>>

    suspend fun getFriendCommunities(communityRequestInfo: CommunityRequestInfo): Result<List<CommunityMainContent>>

    suspend fun updateCommunity(
        communityId: Long,
        communityUpdateInfo: CommunityUpdateInfo,
        communityImages: List<String>): Result<Unit>
    suspend fun deleteCommunity(communityId: Long) : Result<Unit>



}
