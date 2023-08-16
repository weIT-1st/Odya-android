package com.weit.data.source

import com.weit.data.model.ListResponse
import com.weit.data.model.follow.FollowNumDTO
import com.weit.data.service.FollowService
import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowUserIdInfo
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.FollowingSearchInfo
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

class FollowDataSource @Inject constructor(
    private val followService: FollowService,
) {

    private val followers = CopyOnWriteArrayList<FollowUserContent>()
    private val followings = CopyOnWriteArrayList<FollowUserContent>()

    suspend fun createFollow(followFollowingIdInfo: FollowFollowingIdInfo) {
        followService.createFollow(followFollowingIdInfo)
    }

    suspend fun deleteFollow(followFollowingIdInfo: FollowFollowingIdInfo) {
        followService.createFollow(followFollowingIdInfo)
    }

    suspend fun getFollowNumber(
        followUserIdInfo: FollowUserIdInfo,
    ): FollowNumDTO =
        followService.getFollowNumber(
            userId = followUserIdInfo.userId,
        )

    suspend fun getInfiniteFollowing(
        followingSearchInfo: FollowingSearchInfo,
    ): ListResponse<FollowUserContent> {
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
    ): ListResponse<FollowUserContent> {
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
}
