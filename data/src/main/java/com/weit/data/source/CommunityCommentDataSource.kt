package com.weit.data.source

import com.weit.data.model.ListResponse
import com.weit.data.model.community.CommunityCommentContentDTO
import com.weit.data.service.CommunityCommentService
import retrofit2.Response
import javax.inject.Inject

class CommunityCommentDataSource @Inject constructor(
    private val service: CommunityCommentService,
) {

    suspend fun registerCommunityComment(communityId: Long, content:String) {
        service.registerCommunityComment(communityId,content)
    }

    suspend fun getCommunityComments(communityId: Long, size:Int?, lastId:Long?): ListResponse<CommunityCommentContentDTO> {
        return service.getCommunityComments(communityId,size,lastId)
    }

    suspend fun updateCommunityComment(communityId: Long,commentId: Long, content:String): Response<Unit> =
        service.updateCommunityComment(communityId,commentId,content)

    suspend fun deleteCommunityComment(communityId: Long,commentId: Long): Response<Unit> =
        service.deleteCommunityComment(communityId,commentId)
}
