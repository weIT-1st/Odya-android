package com.weit.data.repository.login

import com.weit.data.source.UsernameDataSource
import com.weit.domain.repository.login.GetUserNameRepository
import javax.inject.Inject

class GetUsernameRepositoryImpl @Inject constructor(
    private val userNameDataSource: UsernameDataSource
): GetUserNameRepository {


    override suspend fun setUsername(username: String): Result<Unit> {
         return runCatching{
             userNameDataSource.setUsername(username)
        }
    }

    override suspend fun getUsername(): Result<String?> {
        return runCatching{
            userNameDataSource.getUsername()
        }
    }

}