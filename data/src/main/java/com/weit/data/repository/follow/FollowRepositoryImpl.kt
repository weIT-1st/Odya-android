package com.weit.data.repository.follow

import com.weit.data.model.follow.FollowFollowingId
import com.weit.data.source.FollowDataSource
import com.weit.data.util.exception
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.model.follow.ExperiencedFriendContent
import com.weit.domain.model.follow.ExperiencedFriendInfo
import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.model.follow.FollowNumDetail
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowUserIdInfo
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.FollowingSearchInfo
import com.weit.domain.model.follow.MayknowUserSearchInfo
import com.weit.domain.model.follow.SearchFollowRequestInfo
import com.weit.domain.repository.follow.FollowRepository
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class FollowRepositoryImpl @Inject constructor(
    private val followDataSource: FollowDataSource,
) : FollowRepository {

    private val hasNextFollower = AtomicBoolean(true)
    private val hasNextFollowing = AtomicBoolean(true)
    private val hasNextSearchFollower = AtomicBoolean(true)
    private val hasNextSearchFollowing = AtomicBoolean(true)
    private val hasNextOtherSearchFollower = AtomicBoolean(true)
    private val hasNextOtherSearchFollowing = AtomicBoolean(true)
    private val hasNextUser = AtomicBoolean(true)
    private val hasNextFollowers = AtomicBoolean(true)
    private val hasNextFollowings = AtomicBoolean(true)

    override suspend fun createFollow(followFollowingIdInfo: FollowFollowingIdInfo): Result<Unit> {
        val result = runCatching {
            followDataSource.createFollow(FollowFollowingId(followFollowingIdInfo.followingId))
        }
        return if (result.isSuccess) {
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleFollowError(result.exception()))
        }
    }

    override suspend fun deleteFollow(followFollowingIdInfo: FollowFollowingIdInfo): Result<Unit> {
        val response =
            followDataSource.deleteFollow(FollowFollowingId(followFollowingIdInfo.followingId))
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleDeleteFollowError(response))
        }
    }

    override suspend fun deleteFollower(followerId: Long): Result<Unit> {
        val response =
            followDataSource.deleteFollower(followerId)
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleDeleteFollowError(response))
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
        if (followingSearchInfo.page == 0) {
            hasNextFollowing.set(true)
        }
        if (hasNextFollowing.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            followDataSource.getInfiniteFollowing(followingSearchInfo)
        }
        return if (result.isSuccess) {
            val followSearch = result.getOrThrow()
            hasNextFollowing.set(followSearch.hasNext)
            Result.success(followSearch.content.filterByNickname(query))
        } else {
            Result.failure(result.exception())
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
            Result.failure(result.exception())
        }
    }

    override suspend fun getFollowings(
        followingSearchInfo: FollowingSearchInfo,
    ): Result<List<FollowUserContent>> {
        if (followingSearchInfo.page == 0) {
            hasNextFollowings.set(true)
        }
        if (hasNextFollowings.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            followDataSource.getInfiniteFollowing(followingSearchInfo)
        }
        return if (result.isSuccess) {
            val followSearch = result.getOrThrow()
            hasNextFollowings.set(followSearch.hasNext)
            Result.success(followSearch.content)
        } else {
            Result.failure(result.exception())
        }
    }

    override suspend fun getFollowers(
        followerSearchInfo: FollowerSearchInfo,
    ): Result<List<FollowUserContent>> {
        if (followerSearchInfo.page == 0) {
            hasNextFollowers.set(true)
        }
        if (hasNextFollowers.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            followDataSource.getInfiniteFollower(followerSearchInfo)
        }
        return if (result.isSuccess) {
            val followSearch = result.getOrThrow()
            hasNextFollowers.set(followSearch.hasNext)
            Result.success(followSearch.content)
        } else {
            Result.failure(result.exception())
        }
    }

    override fun getCachedFollower(query: String): List<FollowUserContent> {
        return followDataSource.getCachedFollowers().filterByNickname(query)
    }

    override fun getCachedFollowing(query: String): List<FollowUserContent> {
        return followDataSource.getCachedFollowings().filterByNickname(query)
    }

    override suspend fun getSearchFollowings(searchFollowRequestInfo: SearchFollowRequestInfo): Result<List<FollowUserContent>> {
        if(searchFollowRequestInfo.lastId == null){
            hasNextSearchFollowing.set(true)
        }

        if (hasNextSearchFollowing.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            followDataSource.getSearchFollowings(searchFollowRequestInfo)
        }
        return if (result.isSuccess) {
            val searchUser = result.getOrThrow()
            hasNextSearchFollowing.set(searchUser.hasNext)
            Result.success(searchUser.content)
        } else {
            Result.failure(result.exception())
        }
    }

    override suspend fun getSearchFollowers(searchFollowRequestInfo: SearchFollowRequestInfo): Result<List<FollowUserContent>> {
        if(searchFollowRequestInfo.lastId == null){
            hasNextSearchFollower.set(true)
        }

        if (hasNextSearchFollower.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            followDataSource.getSearchFollowers(searchFollowRequestInfo)
        }
        return if (result.isSuccess) {
            val searchUser = result.getOrThrow()
            hasNextSearchFollower.set(searchUser.hasNext)
            Result.success(searchUser.content)
        } else {
            Result.failure(result.exception())
        }
    }

    override suspend fun getOtherSearchFollowings(
        userId: Long,
        searchFollowRequestInfo: SearchFollowRequestInfo
    ): Result<List<FollowUserContent>> {
        if(searchFollowRequestInfo.lastId == null){
            hasNextOtherSearchFollower.set(true)
        }

        if (hasNextOtherSearchFollower.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            followDataSource.getOtherSearchFollowings(userId,searchFollowRequestInfo)
        }
        return if (result.isSuccess) {
            val searchUser = result.getOrThrow()
            hasNextOtherSearchFollower.set(searchUser.hasNext)
            Result.success(searchUser.content)
        } else {
            Result.failure(result.exception())
        }
    }

    override suspend fun getOtherSearchFollowers(
        userId: Long,
        searchFollowRequestInfo: SearchFollowRequestInfo
    ): Result<List<FollowUserContent>> {
        if(searchFollowRequestInfo.lastId == null){
            hasNextOtherSearchFollowing.set(true)
        }

        if (hasNextOtherSearchFollowing.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            followDataSource.getOtherSearchFollowings(userId,searchFollowRequestInfo)
        }
        return if (result.isSuccess) {
            val searchUser = result.getOrThrow()
            hasNextOtherSearchFollowing.set(searchUser.hasNext)
            Result.success(searchUser.content)
        } else {
            Result.failure(result.exception())
        }
    }

    override suspend fun getMayknowUsers(mayknowUserSearchInfo: MayknowUserSearchInfo): Result<List<FollowUserContent>> {
        if(mayknowUserSearchInfo.lastId == null){
            hasNextUser.set(true)
        }

        if (hasNextUser.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            followDataSource.getMayknowUsers(mayknowUserSearchInfo)
        }
        return if (result.isSuccess) {
            val mayKnowUser = result.getOrThrow()
            hasNextUser.set(mayKnowUser.hasNext)
            Result.success(mayKnowUser.content)
        } else {
            Result.failure(result.exception())
        }
    }

    override suspend fun getExperiencedFriend(placeId: String): Result<ExperiencedFriendInfo> {
        val result = runCatching {
            followDataSource.getExperiencedFriend(placeId)
        }
        return if (result.isSuccess) {
            val info = result.getOrThrow()
            Result.success(
                ExperiencedFriendInfo(
                    info.count,
                    info.followings.map {
                        ExperiencedFriendContent(
                            it.userId,
                            it.nickname,
                            it.profile,
                        )
                    },
                ),
            )
        } else {
            Result.failure(handleFollowError(result.exception()))
        }
    }

    private fun List<FollowUserContent>.filterByNickname(query: String) =
        if (query.isBlank()) {
            this
        } else {
            val nonBlackQuery = query.replace("\\s".toRegex(), "")
            filter { it.nickname.contains(nonBlackQuery, true) }
        }

    private fun handleDeleteFollowError(response: Response<*>): Throwable {
        return handleCode(response.code())
    }

    private fun handleFollowError(t: Throwable): Throwable {
        return if (t is HttpException) {
            handleCode(t.code())
        } else {
            t
        }
    }

    private fun handleCode(code: Int): Throwable {
        return when (code) {
            HTTP_BAD_REQUEST -> InvalidRequestException()
            HTTP_CONFLICT -> ExistedFollowingIdException()
            HTTP_INTERNAL_SERVER_ERROR -> UnKnownException()
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            else -> UnKnownException()
        }
    }
}
