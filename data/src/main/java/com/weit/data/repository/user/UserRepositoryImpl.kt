package com.weit.data.repository.user

import com.weit.data.model.user.UserDTO
import com.weit.data.repository.image.ImageRepositoryImpl
import com.weit.data.source.ImageDataSource
import com.weit.data.source.UserDataSource
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.RegexException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.user.User
import com.weit.domain.repository.user.UserRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.regex.Pattern
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val imageDataSource: ImageDataSource,
    private val imageRepositoryImpl: ImageRepositoryImpl,
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

    override suspend fun updateProfile(uri: String): Result<Unit> {
        val bytes = imageRepositoryImpl.getImageBytes(uri)
        val requestFile = bytes.toRequestBody("image/webp".toMediaType(), 0, bytes.size)
        val file = MultipartBody.Part.createFormData(
            "profile",
            createFileName(uri),
            requestFile,
        )
        val result = userDataSource.updateProfile(file)
        return if (result.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleUserError(result.code()))
        }
    }

    private suspend fun createFileName(uri: String): String {
        return imageDataSource.getImageName(uri) + ".webp"
    }

    private fun handleUserError(responseCode: Int): Throwable {
        return when (responseCode) {
            400 -> InvalidRequestException()
            401 -> InvalidTokenException()
            500 -> UnKnownException()
            else -> UnKnownException()
        }
    }
    private fun getErrorMessage(message: String): String = JSONObject(message)["code"].toString() + JSONObject(message)["errorMessage"].toString()
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
