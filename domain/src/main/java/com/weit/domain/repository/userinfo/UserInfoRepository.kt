package com.weit.domain.repository.userinfo

import java.time.LocalDate

interface UserInfoRepository {
    suspend fun setUsername(
        username: String,
    ): Result<Unit>

    suspend fun getUsername(): Result<String?>

    suspend fun setNickname(
        nickname: String,
    ): Result<Unit>

    suspend fun getNickname(): Result<String?>

    suspend fun setBirth(
        birth: LocalDate,
    ): Result<Unit>
    suspend fun getBirth(): Result<LocalDate?>

    suspend fun setGender(
        gender: String,
    ): Result<Unit>

    suspend fun getGender(): Result<String?>

    suspend fun setTermIdList(
        termIdList: Set<String>,
    ): Result<Unit>

    suspend fun getTermIdList(): Result<Set<String>?>

    suspend fun getIsAlarmAll(): Result<Boolean?>

    suspend fun setIsAlarmAll(
        isAlarmAll : Boolean
    ) : Result<Unit>

    suspend fun getIsAlarmLike(): Result<Boolean?>

    suspend fun setIsAlarmLike(
        isAlarmLike : Boolean
    ) : Result<Unit>

    suspend fun getIsAlarmComment(): Result<Boolean?>

    suspend fun setIsAlarmComment(
        isAlarmComment : Boolean
    ) : Result<Unit>

    suspend fun getIsAlarmMarketing(): Result<Boolean?>

    suspend fun setIsAlarmMarketing(
        isAlarmMarketing : Boolean
    ) : Result<Unit>

    suspend fun getIsLocationInfo(): Result<Boolean?>

    suspend fun setIsLocationInfo(
        isLocationInfo : Boolean
    ) : Result<Unit>
}
