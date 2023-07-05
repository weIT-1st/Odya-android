package com.weit.data.repository.user

import com.weit.data.model.user.UserDTO
import com.weit.data.source.UserDataSource
import com.weit.domain.model.exception.RegexException
import com.weit.domain.model.user.User
import com.weit.domain.repository.user.UserRepository
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
