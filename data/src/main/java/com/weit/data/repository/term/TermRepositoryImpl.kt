package com.weit.data.repository.term

import com.weit.data.model.auth.term.TermIdList
import com.weit.data.source.TermDataSource
import com.weit.data.util.exception
import com.weit.domain.model.auth.AgreedTermInfo
import com.weit.domain.model.auth.TermIdListInfo
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NotExistTermIdException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.repository.term.TermRepository
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class TermRepositoryImpl @Inject constructor(
    private val dataSource: TermDataSource,
) : TermRepository {

    override suspend fun getAgreedTerms(): Result<List<AgreedTermInfo>> {
        val result = runCatching {
            dataSource.getAgreedTerms().userOptionalAgreedTermsList
        }
        return if (result.isSuccess) {
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleTermError(result.exception()))
        }
    }

    override suspend fun setTerms(termIdListInfo: TermIdListInfo): Result<Unit> {
        val response = dataSource.setTerms(
            TermIdList(termIdListInfo.agreedTermsIdList, termIdListInfo.disagreeTermsIdList),
        )
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleSetTermError(response))
        }
    }

    private fun handleSetTermError(response: Response<*>): Throwable {
        return handleCode(response.code())
    }

    private fun handleTermError(t: Throwable): Throwable {
        return if (t is HttpException) {
            handleCode(t.code())
        } else {
            t
        }
    }

    private fun handleCode(code: Int): Throwable {
        return when (code) {
            HTTP_BAD_REQUEST -> InvalidRequestException()
            HTTP_NOT_FOUND -> NotExistTermIdException()
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            else -> UnKnownException()
        }
    }
}
