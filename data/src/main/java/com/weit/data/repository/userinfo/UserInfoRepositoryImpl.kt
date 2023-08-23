package com.weit.data.repository.userinfo

import com.weit.data.source.UserInfoDataSource
import com.weit.domain.repository.userinfo.UserInfoRepository
import java.time.LocalDate
import javax.inject.Inject

class UserInfoRepositoryImpl @Inject constructor(
    private val userinfoDataSource: UserInfoDataSource,
) : UserInfoRepository {

    override suspend fun setUsername(username: String): Result<Unit> {
        return runCatching {
            userinfoDataSource.setUsername(username)
        }
    }

    override suspend fun getUsername(): Result<String?> {
        return runCatching {
            userinfoDataSource.getUsername()
        }
    }

    override suspend fun setNickname(nickname: String): Result<Unit> {
        return runCatching {
            userinfoDataSource.setNickname(nickname)
        }
    }

    override suspend fun getNickname(): Result<String?> {
        return runCatching {
            userinfoDataSource.getNickname()
        }
    }

    override suspend fun setBirth(birth: LocalDate): Result<Unit> {
        return runCatching {
            userinfoDataSource.setBirth(birth)
        }
    }

    override suspend fun getBirth(): Result<LocalDate?> {
        return runCatching {
            userinfoDataSource.getBirth()
        }
    }

    override suspend fun setGender(gender: String): Result<Unit> {
        return runCatching {
            userinfoDataSource.setGender(gender)
        }
    }

    override suspend fun getGender(): Result<String?> {
        return runCatching {
            userinfoDataSource.getGender()
        }
    }
}
