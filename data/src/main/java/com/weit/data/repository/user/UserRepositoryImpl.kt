package com.weit.data.repository.user

import android.util.Patterns
import com.weit.data.model.user.UserDTO
import com.weit.data.source.UserDataSource
import com.weit.domain.model.exception.RegexException
import com.weit.domain.model.user.User
import com.weit.domain.repository.user.UserRepository
import java.util.regex.Pattern
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

        val result : Result<Unit> = runCatching{

            if(Pattern.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", email)){
                userDataSource.updateEmail(email)
            } else {
                throw RegexException()
            }
        }

        return result
    }

    override suspend fun updatePhoneNumber(phonenumber: String): Result<Unit> {
        val result : Result<Unit> = runCatching{

            if (Pattern.matches("^01(?:0|1|[6-9]) - (?:\\d{3}|\\d{4}) - \\d{4}$", "010-0000-0000")) {
                userDataSource.updatePhoneNumber(phonenumber)
            } else {
                throw RegexException()
            }
        }
        return result
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