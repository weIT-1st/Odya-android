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

    override suspend fun updateEmail(newEmail: String): Result<Unit> {
        return runCatching {
            userDataSource.updateEmail(newEmail)
        }
    }

    override suspend fun updatePhoneNumber(newPhonenumber: String): Result<Unit> {
        return runCatching {
            userDataSource.updatePhoneNumber(newPhonenumber)
        }
    }

    override suspend fun updateInformation(newNickname: String): Result<Unit> {
        return runCatching {
            userDataSource.updateInformation(newNickname)
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