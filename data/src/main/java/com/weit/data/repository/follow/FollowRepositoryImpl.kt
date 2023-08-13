package com.weit.data.repository.follow

import com.weit.data.source.FollowDataSource
import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.model.follow.FollowNumDetail
import com.weit.domain.model.follow.FollowSearchDetail
import com.weit.domain.model.follow.FollowUserIdInfo
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.FollowingSearchInfo
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class FollowRepositoryImpl @Inject constructor(
    private val followDataSource: FollowDataSource,
) : FollowRepository {

    override suspend fun createFollow(followFollowingIdInfo: FollowFollowingIdInfo): Result<Unit> {
        return runCatching {
            followDataSource.createFollow(followFollowingIdInfo)
        }
    }

    override suspend fun deleteFollow(followFollowingIdInfo: FollowFollowingIdInfo): Result<Unit> {
        return runCatching {
            followDataSource.deleteFollow(followFollowingIdInfo)
        }
    }

    override suspend fun getFollowNumber(followUserIdInfo: FollowUserIdInfo): Result<FollowNumDetail> {
        return runCatching {
            followDataSource.getFollowNumber(followUserIdInfo)
        }
    }

    override suspend fun getInfiniteFollowing(followingSearchInfo: FollowingSearchInfo): Result<FollowSearchDetail> {
        return runCatching {
            followDataSource.getInfiniteFollowing(followingSearchInfo)
        }
    }

    override suspend fun getInfiniteFollower(followerSearchInfo: FollowerSearchInfo): Result<FollowSearchDetail> {
        return runCatching {
            followDataSource.getInfiniteFollower(followerSearchInfo)
        }
    }
}
