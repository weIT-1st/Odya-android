package com.weit.data.source

import com.weit.data.model.ListResponse
import com.weit.data.model.follow.ExperiencedFriendDTO
import com.weit.data.model.follow.FollowFollowingId
import com.weit.data.model.follow.FollowNumDTO
import com.weit.data.model.follow.FollowUserContentDTO
import com.weit.data.service.FollowService
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowUserIdInfo
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.FollowingSearchInfo
import com.weit.domain.model.follow.MayknowUserSearchInfo
import com.weit.domain.model.follow.SearchFollowRequestInfo
import retrofit2.Response
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

class FollowDataSource @Inject constructor(
    private val followService: FollowService,
) {

    private val followers = CopyOnWriteArrayList<FollowUserContent>()
    private val followings = CopyOnWriteArrayList<FollowUserContent>()

    suspend fun createFollow(followFollowingId: FollowFollowingId) {
        followService.createFollow(followFollowingId)
    }

    suspend fun deleteFollow(followFollowingId: FollowFollowingId): Response<Unit> {
        return followService.deleteFollow(followFollowingId)
    }

    suspend fun deleteFollower(followerId: Long): Response<Unit> {
        return followService.deleteFollower(followerId)
    }

    suspend fun getFollowNumber(
        followUserIdInfo: FollowUserIdInfo,
    ): FollowNumDTO =
        followService.getFollowNumber(
            userId = followUserIdInfo.userId,
        )

    suspend fun getInfiniteFollowing(
        followingSearchInfo: FollowingSearchInfo,
    ): ListResponse<FollowUserContentDTO> {
        val result = followService.getInfiniteFollowing(
            userId = followingSearchInfo.userId,
            page = followingSearchInfo.page,
            size = followingSearchInfo.size,
            sortType = followingSearchInfo.sortType,
        )
        if (followingSearchInfo.page == 0) {
            followings.clear()
        }
        followings.addAll(result.content)
        return result
    }

    suspend fun getInfiniteFollower(
        followerSearchInfo: FollowerSearchInfo,
    ): ListResponse<FollowUserContentDTO> {
        val result = followService.getInfiniteFollower(
            userId = followerSearchInfo.userId,
            page = followerSearchInfo.page,
            size = followerSearchInfo.size,
            sortType = followerSearchInfo.sortType,
        )
        if (followerSearchInfo.page == 0) {
            followers.clear()
        }
        followers.addAll(result.content)
        return result
    }

    fun getCachedFollowers(): List<FollowUserContent> = followers

    fun getCachedFollowings(): List<FollowUserContent> = followings

    suspend fun getSearchFollowings(
        searchFollowRequestInfo: SearchFollowRequestInfo,
    ): ListResponse<FollowUserContentDTO> {
        return followService.searchFollowings(
            searchFollowRequestInfo.size,
            searchFollowRequestInfo.lastId,
            searchFollowRequestInfo.nickname
        )
    }

    suspend fun getSearchFollowers(
        searchFollowRequestInfo: SearchFollowRequestInfo,
    ): ListResponse<FollowUserContentDTO> {
        return followService.searchFollowers(
            searchFollowRequestInfo.size,
            searchFollowRequestInfo.lastId,
            searchFollowRequestInfo.nickname
        )
    }

    suspend fun getOtherSearchFollowings(
        userId: Long,
        searchFollowRequestInfo: SearchFollowRequestInfo,
    ): ListResponse<FollowUserContentDTO> {
        return followService.searchOtherFollowings(
            userId,
            searchFollowRequestInfo.size,
            searchFollowRequestInfo.lastId,
            searchFollowRequestInfo.nickname
        )
    }

    suspend fun getOtherSearchFollowers(
        userId: Long,
        searchFollowRequestInfo: SearchFollowRequestInfo,
    ): ListResponse<FollowUserContentDTO> {
        return followService.searchOtherFollowers(
            userId,
            searchFollowRequestInfo.size,
            searchFollowRequestInfo.lastId,
            searchFollowRequestInfo.nickname
        )
    }

    suspend fun getExperiencedFriend(placeId: String): ExperiencedFriendDTO =
        followService.getExperiencedFriend(placeId)


    suspend fun getMayknowUsers(
        mayknowUserSearchInfo: MayknowUserSearchInfo
    ): ListResponse<FollowUserContentDTO> {
        return followService.getMayknowUsers(
            size = mayknowUserSearchInfo.size,
            lastId = mayknowUserSearchInfo.lastId,
        )
    }

}
