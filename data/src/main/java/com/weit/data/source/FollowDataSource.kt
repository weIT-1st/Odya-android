package com.weit.data.source

import com.weit.data.model.follow.FollowSearchDTO
import com.weit.data.model.follow.FollowNumDTO
import com.weit.data.service.FollowService
import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.model.follow.FollowUserIdInfo
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.FollowingSearchInfo
import javax.inject.Inject

class FollowDataSource @Inject constructor(
    private val followService: FollowService
){

    suspend fun createFollow(followFollowingIdInfo: FollowFollowingIdInfo) {
        followService.createFollow(followFollowingIdInfo)
    }

    suspend fun deleteFollow(followFollowingIdInfo: FollowFollowingIdInfo) {
        followService.createFollow(followFollowingIdInfo)
    }

    suspend fun getFollowNumber( followUserIdInfo: FollowUserIdInfo
    ): FollowNumDTO =
        followService.getFollowNumber(
            userId = followUserIdInfo.userId
        )

    suspend fun getInfiniteFollowing( followingSearchInfo: FollowingSearchInfo
    ): FollowSearchDTO =
        followService.getInfiniteFollowing(
            userId = followingSearchInfo.userId,
            page = followingSearchInfo.page,
            size = followingSearchInfo.size,
            sortType = followingSearchInfo.sortType
        )

    suspend fun getInfiniteFollower(
        followerSearchInfo: FollowerSearchInfo
    ): FollowSearchDTO =
        followService.getInfiniteFollower(
            userId = followerSearchInfo.userId,
            page = followerSearchInfo.page,
            size = followerSearchInfo.size,
            sortType = followerSearchInfo.sortType
        )
}