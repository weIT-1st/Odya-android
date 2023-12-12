package com.weit.data.repository.community

import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.squareup.moshi.Moshi
import com.weit.data.repository.image.ImageRepositoryImpl
import com.weit.data.source.CommunityDataSource
import com.weit.data.source.ImageDataSource
import com.weit.data.util.exception
import com.weit.data.util.getErrorMessage
import com.weit.domain.model.community.CommunityContent
import com.weit.domain.model.community.CommunityMainContent
import com.weit.domain.model.community.CommunityMyActivityCommentContent
import com.weit.domain.model.community.CommunityMyActivityContent
import com.weit.domain.model.community.CommunityRegistrationInfo
import com.weit.domain.model.community.CommunityRequestInfo
import com.weit.domain.model.community.CommunityUpdateInfo
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.community.ExistedCommunityIdException
import com.weit.domain.model.exception.community.NotExistCommunityIdOrCommunityCommentsException
import com.weit.domain.repository.community.comment.CommunityRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_FORBIDDEN
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    private val communityDataSource: CommunityDataSource,
    private val imageRepositoryImpl: ImageRepositoryImpl,
    private val imageDataSource: ImageDataSource,
    private val moshi: Moshi,
    ) : CommunityRepository {

    private val hasNextCommunity = AtomicBoolean(true)
    private val hasNextMyCommunity = AtomicBoolean(true)
    private val hasNextFriendCommunity = AtomicBoolean(true)
    private val hasNextTopicCommunity = AtomicBoolean(true)
    private val hasNextMyLikeCommunity = AtomicBoolean(true)
    private val hasNextMyCommentCommunity = AtomicBoolean(true)


    override suspend fun registerCommunity(
        communityRegistrationInfo: CommunityRegistrationInfo,
        communityImages: List<String>
    ): Result<Unit> {
        val result = runCatching {

            val files = communityImages.map{
                val bytes = imageRepositoryImpl.getImageBytes(it)
                val requestFile = bytes.toRequestBody("image/webp".toMediaType(), 0, bytes.size)
                val fileName = try {
                    imageDataSource.getImageName(it)
                } catch (e: Exception) {
                    return Result.failure(e)
                }
                MultipartBody.Part.createFormData(
                    "community-content-image",
                    "$fileName.webp",
                    requestFile,
                )
            }

            val adapter= moshi.adapter(CommunityRegistrationInfo::class.java)
            val communityInfoJson = adapter.toJson(communityRegistrationInfo)

            val communityRequestBody = communityInfoJson.toRequestBody("application/json".toMediaTypeOrNull())

            val communityPart = MultipartBody.Part.createFormData("community", "community", communityRequestBody)
            communityDataSource.registerCommunity(communityPart,files)
        }
        return if (result.isSuccess) {
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleRegisterAndGetCommentError(result.exception()))
        }
    }

    override suspend fun getCommunities(communityRequestInfo: CommunityRequestInfo): Result<List<CommunityMainContent>> {
        if(communityRequestInfo.lastId == null){
            hasNextCommunity.set(true)
        }

        if (hasNextCommunity.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            communityDataSource.getCommunities(communityRequestInfo.size,communityRequestInfo.lastId,communityRequestInfo.sortType)
        }
        return if (result.isSuccess) {
            val communities = result.getOrThrow()
            hasNextCommunity.set(communities.hasNext)
            Result.success(communities.content)
        } else {
            Result.failure(handleRegisterAndGetCommentError(result.exception()))
        }
    }

    override suspend fun getDetailCommunity(communityId: Long): Result<CommunityContent> {
        val result = runCatching {
            communityDataSource.getDetailCommunity(communityId)
        }
        return if (result.isSuccess) {
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleRegisterAndGetCommentError(result.exception()))
        }
    }

    override suspend fun getMyCommunities(communityRequestInfo: CommunityRequestInfo): Result<List<CommunityMyActivityContent>> {
        if(communityRequestInfo.lastId == null){
            hasNextMyCommunity.set(true)
        }

        if (hasNextMyCommunity.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            communityDataSource.getMyCommunities(communityRequestInfo.size,communityRequestInfo.lastId,communityRequestInfo.sortType)
        }
        return if (result.isSuccess) {
            val communities = result.getOrThrow()
            hasNextMyCommunity.set(communities.hasNext)
            Result.success(communities.content)
        } else {
            Result.failure(handleRegisterAndGetCommentError(result.exception()))
        }
    }

    override suspend fun getFriendCommunities(communityRequestInfo: CommunityRequestInfo): Result<List<CommunityMainContent>> {
        if(communityRequestInfo.lastId == null){
            hasNextFriendCommunity.set(true)
        }

        if (hasNextFriendCommunity.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            communityDataSource.getFriendsCommunities(communityRequestInfo.size,communityRequestInfo.lastId,communityRequestInfo.sortType)
        }
        return if (result.isSuccess) {
            val communities = result.getOrThrow()
            hasNextFriendCommunity.set(communities.hasNext)
            Result.success(communities.content)
        } else {
            Result.failure(handleRegisterAndGetCommentError(result.exception()))
        }
    }

    override suspend fun getCommunitiesByTopic(
        topicId: Long,
        communityRequestInfo: CommunityRequestInfo
    ): Result<List<CommunityMainContent>> {
        if(communityRequestInfo.lastId == null){
            hasNextTopicCommunity.set(true)
        }

        if (hasNextTopicCommunity.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            communityDataSource.getCommunitiesByTopic(topicId, communityRequestInfo.size,communityRequestInfo.lastId,communityRequestInfo.sortType)
        }
        return if (result.isSuccess) {
            val communities = result.getOrThrow()
            hasNextTopicCommunity.set(communities.hasNext)
            Result.success(communities.content)
        } else {
            Result.failure(handleRegisterAndGetCommentError(result.exception()))
        }
    }

    override suspend fun getMyLikeCommunities(communityRequestInfo: CommunityRequestInfo): Result<List<CommunityMyActivityContent>> {
        if(communityRequestInfo.lastId == null){
            hasNextMyLikeCommunity.set(true)
        }

        if (hasNextMyLikeCommunity.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            communityDataSource.getMyLikeCommunities(communityRequestInfo.size,communityRequestInfo.lastId,communityRequestInfo.sortType)
        }
        return if (result.isSuccess) {
            val communities = result.getOrThrow()
            hasNextMyLikeCommunity.set(communities.hasNext)
            Result.success(communities.content)
        } else {
            Result.failure(handleRegisterAndGetCommentError(result.exception()))
        }
    }

    override suspend fun getMyCommentCommunities(communityRequestInfo: CommunityRequestInfo): Result<List<CommunityMyActivityCommentContent>> {
        if(communityRequestInfo.lastId == null){
            hasNextMyCommentCommunity.set(true)
        }

        if (hasNextMyCommentCommunity.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            communityDataSource.getMyCommentCommunities(communityRequestInfo.size,communityRequestInfo.lastId)
        }
        return if (result.isSuccess) {
            val communities = result.getOrThrow()
            hasNextMyCommentCommunity.set(communities.hasNext)
            Result.success(communities.content)
        } else {
            Result.failure(handleRegisterAndGetCommentError(result.exception()))
        }    }

    override suspend fun updateCommunity(
        communityId: Long,
        communityUpdateInfo: CommunityUpdateInfo,
        communityImages: List<String>
    ): Result<Unit> {


            val fileList : MutableList<MultipartBody.Part> = mutableListOf()
            for(uri in communityImages){
                val bytes = imageRepositoryImpl.getImageBytes(uri)
                val requestFile = bytes.toRequestBody("image/webp".toMediaType(), 0, bytes.size)
                val fileName = try {
                    imageDataSource.getImageName(uri)
                } catch (e: Exception) {
                    return Result.failure(e)
                }
                val file = MultipartBody.Part.createFormData(
                    "update-community-content-image",
                    "$fileName.webp",
                    requestFile,
                )

                fileList.add(file)
            }
            val adapter= moshi.adapter(CommunityUpdateInfo::class.java)
            val communityInfoJson = adapter.toJson(communityUpdateInfo)

            val communityRequestBody = communityInfoJson.toRequestBody("application/json".toMediaTypeOrNull())

            val communityPart = MultipartBody.Part.createFormData("update-community", "update-community", communityRequestBody)

        val response = communityDataSource.updateCommunity(communityId,communityPart,fileList)

        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleDeleteError(response))
        }
    }

    override suspend fun deleteCommunity(communityId: Long): Result<Unit> {
        val response = communityDataSource.deleteCommunity(communityId)
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleDeleteError(response))
        }
    }

    override suspend fun registerCommunityLike(communityId: Long): Result<Unit> {
        val response = communityDataSource.registerCommunityLike(communityId)
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleDeleteError(response))
        }
    }

    override suspend fun deleteCommunityLike(communityId: Long): Result<Unit> {
        val response = communityDataSource.deleteCommunityLike(communityId)
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleDeleteError(response))
        }
    }

    private fun handleRegisterAndGetCommentError(t: Throwable): Throwable {
        return if (t is HttpException) {
            Logger.t("MainTest").i("실패 ${t.response()?.getErrorMessage()}")
            handleCode(t.code())
        } else {
            Logger.t("MainTest").i("실패 ${t.message}")
            t
        }
    }

    private fun handleDeleteError(response: Response<*>): Throwable {
        return handleCode(response.code())
    }

    private fun handleCode(code: Int): Throwable {
        return when (code) {
            HTTP_NOT_FOUND -> NotExistCommunityIdOrCommunityCommentsException()
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            HTTP_FORBIDDEN -> InvalidPermissionException()
            HTTP_CONFLICT -> ExistedCommunityIdException()
            HTTP_BAD_REQUEST -> InvalidRequestException()
            else -> {
                UnKnownException()
            }
        }
    }



}
