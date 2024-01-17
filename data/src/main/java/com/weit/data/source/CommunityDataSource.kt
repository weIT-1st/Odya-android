package com.weit.data.source

import com.weit.data.model.ListResponse
import com.weit.data.model.community.CommunityContentDTO
import com.weit.data.model.community.CommunityMainContentDTO
import com.weit.data.model.community.CommunityMyActivityCommentContentDTO
import com.weit.data.model.community.CommunityMyActivityContentDTO
import com.weit.data.service.CommunityService
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class CommunityDataSource @Inject constructor(
    private val service: CommunityService,
) {

    suspend fun registerCommunity(community: MultipartBody.Part, images:List<MultipartBody.Part>) {
        service.registerCommunity(community,images)
    }

    suspend fun updateCommunity(communityId:Long,community: MultipartBody.Part, images:List<MultipartBody.Part>): Response<Unit> {
        return service.updateCommunity(communityId,community,images)
    }

    suspend fun deleteCommunity(communityId: Long): Response<Unit> {
        return service.deleteCommunity(communityId)
    }

    suspend fun getDetailCommunity(communityId: Long) : CommunityContentDTO {
        return service.getDetailCommunity(communityId)
    }

    suspend fun getCommunities(size: Int?, communityId: Long?, sortType: String?, placeId: String?) : ListResponse<CommunityMainContentDTO> {
        return service.getCommunities(size,communityId,sortType,placeId)
    }

    suspend fun getMyCommunities(size: Int?, communityId: Long?, sortType: String?) : ListResponse<CommunityMyActivityContentDTO> {
        return service.getMyCommunities(size,communityId,sortType)
    }

    suspend fun getFriendsCommunities(size: Int?, communityId: Long?, sortType: String?) : ListResponse<CommunityMainContentDTO> {
        return service.getFriendsCommunities(size,communityId,sortType)
    }

    suspend fun getCommunitiesByTopic(topicId: Long, size: Int?, communityId: Long?, sortType: String?) : ListResponse<CommunityMainContentDTO> {
        return service.getCommunitiesByTopic(topicId,size,communityId,sortType)
    }
    suspend fun getMyLikeCommunities(size: Int?, communityId: Long?, sortType: String?) : ListResponse<CommunityMyActivityContentDTO> {
        return service.getMyLikeCommunities(size,communityId,sortType)
    }

    suspend fun getMyCommentCommunities(size: Int?, communityId: Long?) : ListResponse<CommunityMyActivityCommentContentDTO> {
        return service.getMyCommentCommunities(size, communityId)
    }
    
      suspend fun registerCommunityLike(communityId: Long): Response<Unit> {
        return service.registerCommunityLike(communityId)
    }

    suspend fun deleteCommunityLike(communityId: Long): Response<Unit> {
        return service.deleteCommunityLike(communityId)
    }
}
