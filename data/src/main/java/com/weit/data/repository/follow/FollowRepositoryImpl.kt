package com.weit.data.repository.follow

import com.weit.data.source.FollowDataSource
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.model.follow.FollowNumDetail
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowUserIdInfo
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.FollowingSearchInfo
import com.weit.domain.repository.follow.FollowRepository
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class FollowRepositoryImpl @Inject constructor(
    private val followDataSource: FollowDataSource,
) : FollowRepository {

    private val hasNextFollower = AtomicBoolean(true)
    private val hasNextFollowing = AtomicBoolean(true)

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

    override suspend fun getInfiniteFollowing(
        followingSearchInfo: FollowingSearchInfo,
        query: String,
    ): Result<List<FollowUserContent>> {
        if (hasNextFollowing.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            followDataSource.getInfiniteFollowing(followingSearchInfo)
        }
        return if (result.isSuccess) {
            val followSearch = result.getOrThrow()
            hasNextFollowing.set(followSearch.hasNext)
            Result.success(result.getOrThrow().content.filterByNickname(query))
        } else {
            Result.failure(result.exceptionOrNull() ?: UnKnownException())
        }
    }

    override suspend fun getInfiniteFollower(
        followerSearchInfo: FollowerSearchInfo,
        query: String,
    ): Result<List<FollowUserContent>> {
        if (hasNextFollower.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            followDataSource.getInfiniteFollower(followerSearchInfo)
        }
        return if (result.isSuccess) {
            val followSearch = result.getOrThrow()
            hasNextFollower.set(followSearch.hasNext)
            Result.success(followSearch.content.filterByNickname(query))
        } else {
            Result.failure(result.exceptionOrNull() ?: UnKnownException())
        }
    }

    override fun getCachedFollower(query: String): List<FollowUserContent> {
        return followDataSource.getCachedFollowers().filterByNickname(query)
    }

    override fun getCachedFollowing(query: String): List<FollowUserContent> {
        return followDataSource.getCachedFollowings().filterByNickname(query)
    }

    private fun List<FollowUserContent>.filterByNickname(query: String) =
        if (query.isBlank()) {
            this
        } else {
            val nonBlackQuery = query.replace("\\s".toRegex(), "")
            filter { it.nickname.contains(nonBlackQuery, true) }
        }
}
