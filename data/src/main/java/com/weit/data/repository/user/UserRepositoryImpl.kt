package com.weit.data.repository.user

import android.content.res.Resources.NotFoundException
import com.weit.data.model.user.UserContentDTO
import com.weit.data.repository.image.ImageRepositoryImpl
import com.weit.data.source.ImageDataSource
import com.weit.data.source.UserDataSource
import com.weit.data.util.exception
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.RegexException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.user.User
import com.weit.domain.model.user.UserByNickname
import com.weit.domain.model.user.UserByNicknameInfo
import com.weit.domain.model.user.UserContent
import com.weit.domain.repository.user.UserRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.Response
import java.util.regex.Pattern
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val imageDataSource: ImageDataSource,
    private val imageRepositoryImpl: ImageRepositoryImpl,
) : UserRepository {

    override suspend fun getUser(): Result<User> {
        return runCatching {
            userDataSource.getUser()
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

    override suspend fun setUserId(userId: Long) {
        userDataSource.setUserId(userId)
    }

    // 이걸 가져오지 못하면 자신의 UserId가 필요한 기능 수행이 불가능하므로 에러를 throw 함
    override suspend fun getUserId(): Long =
        userDataSource.getUserId() ?: throw NotFoundException()


    override suspend fun getUserByNickname(userByNickname: UserByNickname): Result<UserByNicknameInfo> {
        val result = runCatching {
            userDataSource.getUserByNickname(userByNickname)
        }
        return if (result.isSuccess) {
            val user = result.getOrThrow()
            Result.success(
                UserByNicknameInfo(
                    user.hasNext,
                    user.content.map {
                        UserContent(
                            it.userId,
                            it.nickname,
                            it.profile
                        )
                    }
                ))
        } else {
            Result.failure(result.exception())
        }
    }

    override suspend fun updateProfile(uri: String): Result<Unit> {
        return kotlin.runCatching {
            val bytes = imageRepositoryImpl.getImageBytes(uri)
            val requestFile = bytes.toRequestBody("image/webp".toMediaType(), 0, bytes.size)

            val fileName = try {
                imageDataSource.getImageName(uri)
            } catch (e: Exception) {
                return Result.failure(e)
            }

            val file = MultipartBody.Part.createFormData(
                "profile",
                "$fileName.webp",
                requestFile,
            )

            val result = userDataSource.updateProfile(file)
            if (result.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(handleUserError(result))
            }
        }
    }

    private fun UserContentDTO.toUserContent(): UserContent =
        UserContent(
            userId = userId,
            nickname = nickname,
            profile = profile
        )

    private fun handleUserError(response: Response<Unit>): Throwable {
        return when (response.code()) {
            HTTP_BAD_REQUEST -> InvalidRequestException()
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            HTTP_INTERNAL_SERVER_ERROR -> UnKnownException()
            else -> UnKnownException()
        }
    }

    companion object {
        private const val REGEX_EMAIL =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    }
}
