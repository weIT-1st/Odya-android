package com.weit.data.repository.follow

import com.weit.data.model.follow.FollowNumDTO
import com.weit.data.model.follow.FollowSearchDTO
import com.weit.data.source.FollowDataSource
import com.weit.domain.model.follow.*
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class FollowRepositoryImpl @Inject constructor(
    private val followDataSource: FollowDataSource
): FollowRepository {

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
            followDataSource.getFollowNumber(followUserIdInfo).toFollowNumDetail()
        }
    }

    override suspend fun getInfiniteFollowing(followingSearchInfo: FollowingSearchInfo): Result<FollowSearchDetail> {
        return runCatching {
            followDataSource.getInfiniteFollowing(followingSearchInfo).toFollowSearchDetail()
        }
    }

    override suspend fun getInfiniteFollower(followerSearchInfo: FollowerSearchInfo): Result<FollowSearchDetail> {
        return runCatching {
            followDataSource.getInfiniteFollower(followerSearchInfo).toFollowSearchDetail()
        }
    }

    private fun FollowNumDTO.toFollowNumDetail() =
        FollowNumDetail(
            followerCount = followerCount,
            followingCount = followingCount
        )

    private fun FollowSearchDTO.toFollowSearchDetail() =
        FollowSearchDetail(
            haseNext = hasNext,
            content = content
        )
}


