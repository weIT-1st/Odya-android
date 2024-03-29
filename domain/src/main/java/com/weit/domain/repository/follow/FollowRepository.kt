package com.weit.domain.repository.follow

import com.weit.domain.model.follow.ExperiencedFriendInfo
import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.model.follow.FollowNumDetail
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowUserIdInfo
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.FollowingSearchInfo
import com.weit.domain.model.follow.MayknowUserSearchInfo
import com.weit.domain.model.follow.SearchFollowRequestInfo

interface FollowRepository {

    suspend fun createFollow(
        followFollowingIdInfo: FollowFollowingIdInfo,
    ): Result<Unit>

    suspend fun deleteFollow(
        followFollowingIdInfo: FollowFollowingIdInfo,
    ): Result<Unit>

    suspend fun deleteFollower(
        followerId: Long
    ): Result<Unit>

    suspend fun getFollowNumber(
        followUserIdInfo: FollowUserIdInfo,
    ): Result<FollowNumDetail>

    suspend fun getInfiniteFollowing(
        followingSearchInfo: FollowingSearchInfo,
        query: String,
    ): Result<List<FollowUserContent>>

    suspend fun getFollowers(
        followerSearchInfo: FollowerSearchInfo
        ): Result<List<FollowUserContent>>

    suspend fun getFollowings(
        followingSearchInfo: FollowingSearchInfo
    ): Result<List<FollowUserContent>>

    suspend fun getInfiniteFollower(
        followerSearchInfo: FollowerSearchInfo,
        query: String,
    ): Result<List<FollowUserContent>>

    fun getCachedFollower(
        query: String,
    ): List<FollowUserContent>

    fun getCachedFollowing(
        query: String,
    ): List<FollowUserContent>

    suspend fun getSearchFollowings(
        searchFollowRequestInfo: SearchFollowRequestInfo,
    ): Result<List<FollowUserContent>>

    suspend fun getSearchFollowers(
        searchFollowRequestInfo: SearchFollowRequestInfo,
    ): Result<List<FollowUserContent>>

    suspend fun getOtherSearchFollowings(
        userId: Long,
        searchFollowRequestInfo: SearchFollowRequestInfo,
    ): Result<List<FollowUserContent>>

    suspend fun getOtherSearchFollowers(
        userId: Long,
        searchFollowRequestInfo: SearchFollowRequestInfo,
    ): Result<List<FollowUserContent>>
    suspend fun getMayknowUsers(
        mayknowUserSearchInfo: MayknowUserSearchInfo,
    ): Result<List<FollowUserContent>>

    suspend fun getExperiencedFriend(
        placeId: String,
    ): Result<ExperiencedFriendInfo>
}
