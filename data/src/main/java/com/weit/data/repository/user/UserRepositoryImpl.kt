package com.weit.data.repository.user

import com.weit.data.model.user.UserDTO
import com.weit.data.source.UserDataSource
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.RegexException
import com.weit.domain.model.exception.SomethingErrorException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.user.User
import com.weit.domain.repository.user.UserRepository
import okhttp3.MultipartBody
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import java.util.regex.Pattern
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
) : UserRepository {

    override suspend fun getUser(): Result<User> {
        return runCatching {
            userDataSource.getUser().toUser()
        }
    }

    override suspend fun updateEmail(emailUpdateUser: User): Result<Unit> {
        val result: Result<Unit> = runCatching {
            if (Pattern.matches(REGEX_EMAIL, emailUpdateUser.email.toString())) {
                userDataSource.updateEmail(emailUpdateUser)
            } else {
                throw RegexException()
            }
        }
        return result
    }

    override suspend fun updatePhoneNumber(phoneNumberUpdateUser: User): Result<Unit> {
        return runCatching {
            userDataSource.updatePhoneNumber(phoneNumberUpdateUser)
        }
    }

    override suspend fun updateInformation(informationUpdateUser: User): Result<Unit> {
        return runCatching {
            userDataSource.updateInformation(informationUpdateUser)
        }
    }

    override suspend fun updateProfile(file: MultipartBody.Part): Result<Unit> {
        return handleUserResult {
            userDataSource.updateProfile(file)
        }
    }

    private inline fun <T> handleUserResult(
        block: () -> T
    ): Result<T> {
        return try {
            val result = runCatching(block)
            if (result.isSuccess) {
                Result.success(result.getOrThrow())
            } else {
                Result.failure(handleUserError(result.exceptionOrNull()!!))
            }
        } catch (t: Throwable) {
            Result.failure(handleUserError(t))
        }
    }

    private fun handleUserError(t: Throwable): Throwable {
        return if (t is HttpException) {
            when (t.code()) {
                HTTP_BAD_REQUEST -> InvalidRequestException()
                HTTP_INTERNAL_SERVER_ERROR -> SomethingErrorException()
                HTTP_UNAUTHORIZED -> InvalidTokenException()
                else -> UnKnownException()
            }
        } else {
            t
        }
    }

    private fun UserDTO.toUser() =
        User(
            userID = userID,
            nickname = nickname,
            email = email,
            phoneNumber = phoneNumber,
            gender = gender,
            birthday = birthday,
            socialType = socialType,
        )

    companion object {
        private const val REGEX_EMAIL = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    }
}
