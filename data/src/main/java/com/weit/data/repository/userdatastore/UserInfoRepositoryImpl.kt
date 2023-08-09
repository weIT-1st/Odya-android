package com.weit.data.repository.userdatastore

import com.weit.data.source.UserinfoDataSource
import com.weit.domain.repository.login.UserInfoRepository
import javax.inject.Inject

class UserInfoRepositoryImpl @Inject constructor(
    private val userinfoDataSource: UserinfoDataSource
): UserInfoRepository {


    override suspend fun setUsername(username: String): Result<Unit> {
         return runCatching{
             userinfoDataSource.setUsername(username)
        }
    }

    override suspend fun getUsername(): Result<String?> {
        return runCatching{
            userinfoDataSource.getUsername()
        }
    }


    override suspend fun setNickname(nickname: String): Result<Unit> {
        return runCatching{
            userinfoDataSource.setNickname(nickname)
        }
    }

    override suspend fun getNickname(): Result<String?> {
        return runCatching{
            userinfoDataSource.getNickname()
        }
    }
}