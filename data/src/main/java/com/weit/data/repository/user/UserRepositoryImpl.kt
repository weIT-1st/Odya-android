package com.weit.data.repository.user

import android.content.res.Resources.NotFoundException
import com.weit.data.repository.image.ImageRepositoryImpl
import com.weit.data.source.ImageDataSource
import com.weit.data.source.UserDataSource
import com.weit.data.source.UserInfoDataSource
import com.weit.data.util.exception
import com.weit.domain.model.exception.ImageNotFoundException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.RegexException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.domain.model.user.LifeshotRequestInfo
import com.weit.domain.model.user.SearchUserContent
import com.weit.domain.model.user.SearchUserRequestInfo
import com.weit.domain.model.user.User
import com.weit.domain.model.user.UserStatistics
import com.weit.domain.repository.user.UserRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val imageDataSource: ImageDataSource,
    private val imageRepositoryImpl: ImageRepositoryImpl,
    private val userInfoDataSource: UserInfoDataSource,
) : UserRepository {
    private val hasNextUser = AtomicBoolean(true)
    private val hasNextImage = AtomicBoolean(true)

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
        userInfoDataSource.setUserId(userId)
    }

    // 이걸 가져오지 못하면 자신의 UserId가 필요한 기능 수행이 불가능하므로 에러를 throw 함
    override suspend fun getUserId(): Long =
        userInfoDataSource.getUserId() ?: throw NotFoundException()

    override suspend fun deleteUser(): Result<Unit> {
        val result = userDataSource.deleteUser()
        return if (result.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleUserError(result))
        }
    }



    override suspend fun searchUser(searchUserRequestInfo: SearchUserRequestInfo): Result<List<SearchUserContent>> {
        if(searchUserRequestInfo.lastId == null){
            hasNextUser.set(true)
        }

        if (hasNextUser.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            userDataSource.searchUser(searchUserRequestInfo)
        }
        return if (result.isSuccess) {
            val users = result.getOrThrow()
            hasNextUser.set(users.hasNext)
            Result.success(users.content)
        } else {
            Result.failure(handleGetError(result.exception()))
        }
    }

    override suspend fun getUserStatistics(userId: Long): Result<UserStatistics> {
        val result = runCatching { userDataSource.getUserStatistics(userId) }
        return if (result.isSuccess) {
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleGetError(result.exception()))
        }
    }

    override suspend fun getUserLifeShot(lifeshotRequestInfo: LifeshotRequestInfo): Result<List<UserImageResponseInfo>> {
        if(lifeshotRequestInfo.lastId == null){
            hasNextImage.set(true)
        }

        if (hasNextImage.get().not()){
            return Result.failure(NoMoreItemException())
        }

        val result = kotlin.runCatching { userDataSource.getUserLifeshots(lifeshotRequestInfo) }
        return if (result.isSuccess){
            val list = result.getOrThrow()
            Result.success(list.content.map {
                UserImageResponseInfo(
                    it.imageId,
                    it.imageUrl,
                    it.placeId,
                    it.isLifeShot,
                    it.placeName,
                    it.journalId,
                    it.communityId
                )
            })
        } else {
            Result.failure(handleGetError(result.exception()))
        }
    }

    private suspend fun getMultipartFile(uri: String): MultipartBody.Part{
        val bytes = imageRepositoryImpl.getImageBytes(uri)
        val requestFile = bytes.toRequestBody("image/webp".toMediaType(), 0, bytes.size)

        val fileName = try {
            imageDataSource.getImageName(uri)
        } catch (e: Exception) {
            throw e
        }

        return MultipartBody.Part.createFormData(
            "profile",
            "$fileName.webp",
            requestFile,
        )
    }
    override suspend fun updateProfile(uri: String?): Result<Unit> {
        return kotlin.runCatching {
            val file: MultipartBody.Part = if (uri != null) {
                getMultipartFile(uri)
            } else {
                MultipartBody.Part.createFormData("profile", "")
            }

            val result = userDataSource.updateProfile(file)
            if (result.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(handleUserError(result))
            }
        }.getOrElse {
            Result.failure(ImageNotFoundException())
        }
    }

    private fun handleGetError(t: Throwable): Throwable {
        return if (t is HttpException) {
            handleCode(t.code())
        } else {
            t
        }
    }

    private fun handleCode(code: Int): Throwable {
        return when (code) {
            HTTP_BAD_REQUEST -> InvalidRequestException()
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            else -> {
                UnKnownException()
            }
        }
    }

    private fun handleUserError(response: Response<Unit>): Throwable {
        return handleCode(response.code())
    }

    companion object {
        private const val REGEX_EMAIL = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    }
}
