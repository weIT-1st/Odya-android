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

    override suspend fun setTermIdList(termIdList: Set<String>): Result<Unit> {
        return runCatching {
            userinfoDataSource.setTermIdList(termIdList)
        }
    }

    override suspend fun getTermIdList(): Result<Set<String>?> {
        return runCatching {
            userinfoDataSource.getTermIdList()
        }
    }

    override suspend fun getIsAlarmAll(): Result<Boolean?> {
        return runCatching {
            userinfoDataSource.getIsAlarmAll()
        }
    }

    override suspend fun setIsAlarmAll(isAlarmAll: Boolean): Result<Unit> {
        return runCatching {
            userinfoDataSource.setIsAlarmAll(isAlarmAll)
        }
    }

    override suspend fun getIsAlarmLike(): Result<Boolean?> {
        return runCatching {
            userinfoDataSource.getIsAlarmLike()
        }
    }

    override suspend fun setIsAlarmLike(isAlarmLike: Boolean): Result<Unit> {
        return runCatching {
            userinfoDataSource.setIsAlarmLike(isAlarmLike)
        }
    }

    override suspend fun getIsAlarmComment(): Result<Boolean?> {
        return runCatching {
            userinfoDataSource.getIsAlarmComment()
        }
    }

    override suspend fun setIsAlarmComment(isAlarmComment: Boolean): Result<Unit> {
        return runCatching {
            userinfoDataSource.setIsAlarmComment(isAlarmComment)
        }
    }

    override suspend fun getIsAlarmMarketing(): Result<Boolean?> {
        return runCatching {
            userinfoDataSource.getIsAlarmMarketing()
        }
    }

    override suspend fun setIsAlarmMarketing(isAlarmMarketing: Boolean): Result<Unit> {
        return runCatching {
            userinfoDataSource.setIsAlarmMarketing(isAlarmMarketing)
        }
    }

    override suspend fun getIsLocationInfo(): Result<Boolean?> {
        return runCatching {
            userinfoDataSource.getIsLocationInfo()
        }
    }

    override suspend fun setIsLocationInfo(isLocationInfo: Boolean): Result<Unit> {
        return runCatching {
            userinfoDataSource.setIsLocationInfo(isLocationInfo)
        }
    }
}
