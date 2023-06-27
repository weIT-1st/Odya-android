package com.weit.data.repository.user

import com.weit.data.model.user.UserDTO
import com.weit.data.source.UserDataSource
import com.weit.domain.model.user.User
import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
): UserRepository {

    override suspend fun getUser(): Result<User> {
        return runCatching {
            userDataSource.getUser().toUser()
        }
    }

    override suspend fun updateEmail(email: String): Result<Unit> {
        return runCatching {
            userDataSource.updateEmail(email)
        }
    }

    override suspend fun updatePhoneNumber(phonenumber: String): Result<Unit> {
        return runCatching {
            userDataSource.updatePhoneNumber(phonenumber)
        }
    }

    override suspend fun updateInformation(nickname: String): Result<Unit> {
        return runCatching {
            userDataSource.updateInformation(nickname)
        }
    }


    private fun UserDTO.toUser() =
        User(
            userID= userID,
            nickname = nickname,
            email = email,
            phoneNumber = phoneNumber,
            gender = gender,
            birthday = birthday,
            socialType = socialType
        )
}