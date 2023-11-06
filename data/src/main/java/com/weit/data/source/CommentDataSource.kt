package com.weit.data.source

import com.weit.data.model.ListResponse
import com.weit.data.model.community.CommentContentDTO
import com.weit.data.model.community.CommentRegistration
import com.weit.data.service.CommentService
import retrofit2.Response
import javax.inject.Inject

class CommentDataSource @Inject constructor(
    private val service: CommentService,
) {

    suspend fun registerCommunityComment(communityId: Long, content:String) {
        service.registerCommunityComment(communityId, CommentRegistration(content))
    }

    suspend fun getCommunityComments(communityId: Long, size:Int?, lastId:Long?): ListResponse<CommentContentDTO> {
        return service.getCommunityComments(communityId,size,lastId)
    }

    suspend fun updateCommunityComment(communityId: Long,commentId: Long, content:String): Response<Unit> =
        service.updateCommunityComment(communityId,commentId,CommentRegistration(content))

    suspend fun deleteCommunityComment(communityId: Long,commentId: Long): Response<Unit> =
        service.deleteCommunityComment(communityId,commentId)
}
