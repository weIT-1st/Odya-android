package com.weit.data.repository.place

import com.weit.data.source.PlaceSearchHistoryDataSource
import com.weit.data.util.exception
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.repository.place.PlaceSearchHistoryRepository
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class PlaceSearchHistoryRepositoryImpl @Inject constructor(
    private val dataSource: PlaceSearchHistoryDataSource
): PlaceSearchHistoryRepository {
    override suspend fun setRecentPlaceSearch(searchTermList: List<String>): Result<Unit> {
        return runCatching {
            dataSource.setRecentPlaceSearch(searchTermList.toSet())
        }
    }

    override suspend fun getRecentPlaceSearch(): Result<List<String>?> {
        val result =  runCatching { dataSource.getRecentPlaceSearch() }
        return if (result.isSuccess){
            val list = result.getOrThrow()?.toList()?: emptyList()
            Result.success(list)
        } else {
            Result.failure(handleReviewError(result.exception()))
        }
    }

    override suspend fun registerPlaceSearchHistory(searchTerm: String): Result<Unit> {
        val response = dataSource.registerPlaceSearchHistory(searchTerm)

        return if (response.isSuccessful){
            Result.success(Unit)
        } else {
            Result.failure(handleReviewError(handleResponseError(response)))
        }
    }

    override suspend fun getPlaceSearchHistoryRank(): Result<List<String>> {
        val result = runCatching { dataSource.getPlaceSearchHistoryRank() }

        return if (result.isSuccess){
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleReviewError(result.exception()))
        }
    }

    override suspend fun getPlaceSearchHistoryAgeRank(ageRange: Int?): Result<List<String>> {
        val result = runCatching { dataSource.getPlaceSearchHistoryAgeRank(ageRange) }

        return if (result.isSuccess){
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleReviewError(result.exception()))
        }
    }


    private fun handleReviewError(t: Throwable): Throwable {
        return if (t is HttpException) {
            handleCode(t.code())
        } else {
            t
        }
    }

    private fun handleCode(code: Int): Throwable {
        return when (code) {
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            HTTP_BAD_REQUEST -> InvalidRequestException()
            else -> UnKnownException()
        }
    }

    private fun handleResponseError(response: Response<*>): Throwable {
        return handleCode(response.code())
    }
}
