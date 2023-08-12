package com.weit.data.repository.user

import android.content.res.Resources.NotFoundException
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

    override suspend fun setUserId(userId: Int) {
        userDataSource.setUserId(userId)
    }

    // 이걸 가져오지 못하면 자신의 UserId가 필요한 기능 수행이 불가능하므로 에러를 throw 함
    override suspend fun getUserId(): Int =
        userDataSource.getUserId() ?: throw NotFoundException()

    companion object {
        private const val REGEX_EMAIL = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    }
}
