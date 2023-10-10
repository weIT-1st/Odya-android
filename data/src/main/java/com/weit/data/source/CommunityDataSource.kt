package com.weit.data.source

import com.weit.data.model.ListResponse
import com.weit.data.model.community.CommunityCommentContentDTO
import com.weit.data.model.community.CommunityContentDTO
import com.weit.data.model.community.CommunityMainContentDTO
import com.weit.data.model.community.CommunityRegistration
import com.weit.data.model.follow.CommunityId
import com.weit.data.model.follow.FollowFollowingId
import com.weit.data.service.CommunityCommentService
import com.weit.data.service.CommunityService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class CommunityDataSource @Inject constructor(
    private val service: CommunityService,
) {

    suspend fun registerCommunity(community: MultipartBody.Part, images:List<MultipartBody.Part>) {
        service.registerCommunity(community,images)
    }

    suspend fun updateCommunity(communityId:Long,community: MultipartBody.Part, images:List<MultipartBody.Part>) {
        service.updateCommunity(communityId,community,images)
    }

    suspend fun deleteCommunity(communityId: Long): Response<Unit> {
        return service.deleteCommunity(communityId)
    }

    suspend fun getDetailCommunity(communityId: Long) : CommunityContentDTO {
        return service.getDetailCommunity(communityId)
    }

    suspend fun getCommunities(size: Int?, communityId: Long?, sortType: String?) : ListResponse<CommunityMainContentDTO> {
        return service.getCommunities(size,communityId,sortType)
    }

    suspend fun getMyCommunities(size: Int?, communityId: Long?, sortType: String?) : ListResponse<CommunityMainContentDTO> {
        return service.getMyCommunities(size,communityId,sortType)
    }

    suspend fun getFriendsCommunities(size: Int?, communityId: Long?, sortType: String?) : ListResponse<CommunityMainContentDTO> {
        return service.getFriendsCommunities(size,communityId,sortType)
    }


}
