package com.weit.domain.repository.follow

import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.model.follow.FollowNumDetail
import com.weit.domain.model.follow.FollowSearchDetail
import com.weit.domain.model.follow.FollowUserIdInfo
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.FollowingSearchInfo

interface FollowRepository {

    suspend fun createFollow(
        followFollowingIdInfo: FollowFollowingIdInfo,
    ): Result<Unit>

    suspend fun deleteFollow(
        followFollowingIdInfo: FollowFollowingIdInfo,
    ): Result<Unit>

    suspend fun getFollowNumber(
        followUserIdInfo: FollowUserIdInfo,
    ): Result<FollowNumDetail>

    suspend fun getInfiniteFollowing(
        followingSearchInfo: FollowingSearchInfo,
    ): Result<FollowSearchDetail>

    suspend fun getInfiniteFollower(
        followerSearchInfo: FollowerSearchInfo,
    ): Result<FollowSearchDetail>
}
