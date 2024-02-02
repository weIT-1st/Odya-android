package com.weit.data.repository.journal

import android.content.res.Resources.NotFoundException
import com.squareup.moshi.Moshi
import com.weit.data.model.ListResponse
import com.weit.data.model.journal.TravelJournalCompanionsDTO
import com.weit.data.model.journal.TravelJournalContentsDTO
import com.weit.data.model.journal.TravelJournalContentsImagesDTO
import com.weit.data.model.journal.TravelJournalDTO
import com.weit.data.model.journal.TravelJournalListDTO
import com.weit.data.model.journal.TravelJournalVisibility
import com.weit.data.model.journal.TravelJournalWriterDTO
import com.weit.data.source.ImageDataSource
import com.weit.data.source.TravelJournalDataSource
import com.weit.data.util.exception
import com.weit.domain.model.exception.ForbiddenException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.journal.TravelCompanionSimpleResponsesInfo
import com.weit.domain.model.journal.TravelJournalCompanionsInfo
import com.weit.domain.model.journal.TravelJournalContentUpdateInfo
import com.weit.domain.model.journal.TravelJournalContentsImagesInfo
import com.weit.domain.model.journal.TravelJournalContentsInfo
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.model.journal.TravelJournalRegistrationInfo
import com.weit.domain.model.journal.TravelJournalUpdateInfo
import com.weit.domain.model.journal.TravelJournalVisibilityInfo
import com.weit.domain.model.journal.TravelJournalWriterInfo
import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.repository.image.ImageRepository
import com.weit.domain.repository.journal.TravelJournalRepository
import com.weit.domain.repository.place.PlaceRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_FORBIDDEN
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class TravelJournalRepositoryImpl @Inject constructor(
    private val travelJournalDataSource: TravelJournalDataSource,
    private val imageRepository: ImageRepository,
    private val imageDataSource: ImageDataSource,
    private val placeRepository: PlaceRepository,
    private val moshi: Moshi
) : TravelJournalRepository {

    private val hasNextJournal = AtomicBoolean(true)
    private val hasNextMyJournal = AtomicBoolean(true)
    private val hasNextFriendJournal = AtomicBoolean(true)
    private val hasNextRecommendJournal = AtomicBoolean(true)
    private val hasNextTaggedJournal = AtomicBoolean(true)

    override suspend fun registerTravelJournal(
        travelJournalRegistrationInfo: TravelJournalRegistrationInfo,
        travelJournalImages: List<String>
    ): Result<Unit> {
        val result = runCatching {
            val files = travelJournalImages.map {
                val requestFile = imageDataSource.getRequestBody(it)
                val fileName = try {
                    imageDataSource.getImageName(it)
                } catch (e: Exception) {
                    return Result.failure(e)
                }
                MultipartBody.Part.createFormData(
                    TRAVEL_JOURNAL_CONTENT_IMAGE,
                    "$fileName.webp",
                    requestFile
                )
            }
            val adapter = moshi.adapter(TravelJournalRegistrationInfo::class.java)
            val travelJournalInfoJson = adapter.toJson(travelJournalRegistrationInfo)

            val travelJournalRequestBody =
                travelJournalInfoJson.toRequestBody("application/json".toMediaTypeOrNull())

            val travelJournalPart = MultipartBody.Part.createFormData(
                TRAVEL_JOURNAL,
                TRAVEL_JOURNAL,
                travelJournalRequestBody
            )
            travelJournalDataSource.registerTravelJournal(travelJournalPart, files)
        }
        return if (result.isSuccess) {
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleJournalError(result.exception()))
        }
    }


    override suspend fun getTravelJournal(travelJournalId: Long): Result<TravelJournalInfo> {
        val result = runCatching { travelJournalDataSource.getTravelJournal(travelJournalId) }

        return if (result.isSuccess) {
            val journal = result.getOrThrow()
            Result.success(journal.toTravelJournalInfo())
        } else {
            Result.failure(handleJournalError(result.exception()))
        }
    }

    override suspend fun getTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): Result<List<TravelJournalListInfo>> =
        getInfiniteJournalList(
            hasNextJournal,
            lastTravelJournal,
            runCatching { travelJournalDataSource.getTravelJournalList(size, lastTravelJournal) }
        )

    override suspend fun getMyTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?,
        placeId: String?
    ): Result<List<TravelJournalListInfo>> =
        getInfiniteJournalList(
            hasNextMyJournal,
            lastTravelJournal,
            runCatching {
                travelJournalDataSource.getMyTravelJournalList(
                    size,
                    lastTravelJournal,
                    placeId
                )
            }
        )

    override suspend fun getFriendTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?,
        placeId: String?,
    ): Result<List<TravelJournalListInfo>> =
        getInfiniteJournalList(
            hasNextFriendJournal,
            lastTravelJournal,
            runCatching {
                travelJournalDataSource.getFriendTravelJournalList(
                    size,
                    lastTravelJournal,
                    placeId
                )
            }
        )

    override suspend fun getRecommendTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?,
        placeId: String?,
    ): Result<List<TravelJournalListInfo>> =
        getInfiniteJournalList(
            hasNextRecommendJournal,
            lastTravelJournal,
            runCatching {
                travelJournalDataSource.getRecommendTravelJournalList(
                    size,
                    lastTravelJournal,
                    placeId
                )
            }
        )



    override suspend fun getTaggedTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): Result<List<TravelJournalListInfo>> =
        getInfiniteJournalList(
            hasNextTaggedJournal,
            lastTravelJournal,
            runCatching { travelJournalDataSource.getTaggedTravelJournalList(size, lastTravelJournal) }
        )

    // 여행일지 수정 Api
    override suspend fun updateTravelJournal(
        travelJournalId: Long,
        travelJournalUpdateInfo: TravelJournalUpdateInfo,
    ): Result<Unit> {
        val result = runCatching {
            val adapter = moshi.adapter(TravelJournalUpdateInfo::class.java)
            val travelJournalUpdateInfoJson = adapter.toJson(travelJournalUpdateInfo)

            val travelJournalUpdateRequestBody =
                travelJournalUpdateInfoJson.toRequestBody("application/json".toMediaTypeOrNull())

            val travelJournalUpdatePart = MultipartBody.Part.createFormData(
                TRAVEL_JOURNAL_UPDATE,
                TRAVEL_JOURNAL_UPDATE,
                travelJournalUpdateRequestBody
            )
            travelJournalDataSource.updateTravelJournal(travelJournalId, travelJournalUpdatePart)
        }

        return if (result.isSuccess) {
            Result.success(result.getOrThrow())
        } else {
            Result.failure(result.exception())
        }
    }

    // 여행일지 콘텐츠 수정 Api
    override suspend fun updateTravelJournalContent(
        travelJournalId: Long,
        travelJournalContentId: Long,
        travelJournalContentUpdateInfo: TravelJournalContentUpdateInfo,
        travelJournalContentImages: List<String>
    ): Result<Unit> {
        val result = runCatching {
            val files = travelJournalContentImages.map {
                val requestFile = imageDataSource.getRequestBody(it)
                val fileName = try {
                    imageDataSource.getImageName(it)
                } catch (e: Exception) {
                    return Result.failure(e)
                }
                MultipartBody.Part.createFormData(
                    TRAVEL_JOURNAL_CONTENT_IMAGE_UPDATE,
                    "$fileName.webp",
                    requestFile
                )
            }
            val adapter = moshi.adapter(TravelJournalContentUpdateInfo::class.java)
            val travelJournalContentUpdateInfoJson = adapter.toJson(travelJournalContentUpdateInfo)

            val travelJournalContentRequestBody =
                travelJournalContentUpdateInfoJson.toRequestBody("application/json".toMediaTypeOrNull())

            val travelJournalContentPart = MultipartBody.Part.createFormData(
                TRAVEL_JOURNAL_CONTENT_UPDATE,
                TRAVEL_JOURNAL_CONTENT_UPDATE,
                travelJournalContentRequestBody
            )
            travelJournalDataSource.updateTravelJournalContent(
                travelJournalId,
                travelJournalContentId,
                travelJournalContentPart,
                files
            )
        }

        return if (result.isSuccess) {
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleJournalError(result.exception()))
        }
    }

    override suspend fun updateTravelJournalVisibility(travelJournalVisibilityInfo: TravelJournalVisibilityInfo): Result<Unit> {
        val response =
            travelJournalDataSource.updateTravelJournalVisibility(
                travelJournalVisibilityInfo.travelJournalId,
                TravelJournalVisibility(travelJournalVisibilityInfo.visibility)
            )

        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleJournalError((handleResponseError(response))))
        }
    }

    override suspend fun deleteTravelJournal(travelJournalId: Long): Result<Unit> =
        delete(travelJournalDataSource.deleteTravelJournal(travelJournalId))

    override suspend fun deleteTravelJournalContent(
        travelJournalId: Long,
        travelJournalContentId: Long
    ): Result<Unit> =
        delete(
            travelJournalDataSource.deleteTravelJournalContent(
                travelJournalId,
                travelJournalContentId
            )
        )

    override suspend fun deleteTravelJournalFriend(travelJournalId: Long): Result<Unit> =
        delete(travelJournalDataSource.deleteTravelJournalFriend(travelJournalId))

    private suspend fun getInfiniteJournalList(
        hasNext: AtomicBoolean,
        lastId: Long?,
        result: Result<ListResponse<TravelJournalListDTO>>
    ): Result<List<TravelJournalListInfo>> {
        if (lastId == null) {
            hasNext.set(true)
        }

        if (hasNext.get().not()) {
            return Result.failure(NoMoreItemException())
        }

        return if (result.isSuccess) {
            val listSearch = result.getOrThrow()
            hasNext.set(listSearch.hasNext)
            Result.success(listSearch.content.map {
                TravelJournalListInfo(
                    it.travelJournalId,
                    it.travelJournalTitle,
                    it.testContent,
                    it.contentImageUrl,
                    it.travelStartDate,
                    it.travelEndDate,
                    it.placeIds.map { placeId ->
                        placeRepository.getPlaceDetail(placeId).getOrThrow()
                    },
                    TravelJournalWriterInfo(
                        it.writer.userId,
                        it.writer.nickname,
                        it.writer.profile
                    ),
                    it.visibility,
                    it.travelCompanionSimpleResponses.map { response ->
                        TravelCompanionSimpleResponsesInfo(
                            response.username,
                            response.profileUrl
                        )
                    },
                    it.isBookmark
                )
            })
        } else {
            Result.failure(handleJournalError(result.exception()))
        }
    }

    private fun delete(response: Response<Unit>): Result<Unit> =
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleJournalError((handleResponseError(response))))
        }

    private fun handleResponseError(response: Response<*>): Throwable {
        return handleCode(response.code())
    }

    private fun handleJournalError(t: Throwable): Throwable {
        return if (t is HttpException) {
            handleCode(t.code())
        } else {
            t
        }
    }

    private fun handleCode(code: Int): Throwable {
        return when (code) {
            HTTP_FORBIDDEN -> ForbiddenException()  // 403
            HTTP_UNAUTHORIZED -> InvalidTokenException()// 401
            HTTP_BAD_REQUEST -> InvalidRequestException() // 400
            HTTP_NOT_FOUND -> NotFoundException() // 404
            else -> UnKnownException()
        }
    }

    private suspend fun TravelJournalDTO.toTravelJournalInfo(): TravelJournalInfo =
        TravelJournalInfo(
            travelJournalId = travelJournalId,
            travelJournalTitle = travelJournalTitle,
            travelStartDate = travelStartDate,
            travelEndDate = travelEndDate,
            visibility = visibility,
            isBookmarked = isBookmarked,
            writer = writer.toTravelJournalWriterInfo(),
            travelJournalContents.map { it.toTravelJournalContentsInfo() },
            travelJournalCompanions.map { it.toTravelJournalCompanionsInfo() }
        )

    private fun TravelJournalWriterDTO.toTravelJournalWriterInfo(): TravelJournalWriterInfo =
        TravelJournalWriterInfo(
            userId = userId,
            nickname = nickname,
            profile = profile
        )

    private suspend fun  TravelJournalContentsDTO.toTravelJournalContentsInfo(): TravelJournalContentsInfo =
        TravelJournalContentsInfo(
            travelJournalContentId = travelJournalContentId,
            content = content,
            placeDetail = if (placeId == null){
                // placeId를 입력받지 못한 경우
                PlaceDetail("", null, null, null, null)
            } else {
                placeRepository.getPlaceDetail(placeId).getOrThrow()
                   },
            latitude = latitude,
            longitude = longitude,
            travelDate = travelDate,
            travelJournalContentImages.map { it.toTravelJournalContentsImagesInfo() }
        )

    private fun TravelJournalContentsImagesDTO.toTravelJournalContentsImagesInfo(): TravelJournalContentsImagesInfo =
        TravelJournalContentsImagesInfo(
            travelJournalContentImageId = travelJournalContentImageId,
            contentImageName = contentImageName,
            contentImageUrl = contentImageUrl
        )

    private fun TravelJournalCompanionsDTO.toTravelJournalCompanionsInfo(): TravelJournalCompanionsInfo =
        TravelJournalCompanionsInfo(
            userId = userId,
            nickname = nickname,
            profileUrl = profileUrl,
            isRegistered = isRegistered,
            isFollowing = isFollowing
        )

    companion object {
        private const val TRAVEL_JOURNAL_CONTENT_IMAGE = "travel-journal-content-image"
        private const val TRAVEL_JOURNAL = "travel-journal"
        private const val TRAVEL_JOURNAL_UPDATE = "travel-journal-update"
        private const val TRAVEL_JOURNAL_CONTENT_IMAGE_UPDATE =
            "travel-journal-content-image-update"
        private const val TRAVEL_JOURNAL_CONTENT_UPDATE = "travel-journal-content-update"
    }
}
