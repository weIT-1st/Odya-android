package com.weit.domain.repository.userinfo

import java.time.LocalDate
import javax.naming.BinaryRefAddr

interface UserInfoRepository {
    suspend fun setUsername(
        username: String
    ): Result<Unit>

    suspend fun getUsername(): Result<String?>

    suspend fun setNickname(
        nickname: String
    ): Result<Unit>

    suspend fun getNickname(): Result<String?>

    suspend fun setBirth(
        birth: LocalDate
    ): Result<Unit>
    suspend fun getBirth(): Result<LocalDate?>

    suspend fun setGender(
        gender: String
    ): Result<Unit>

    suspend fun getGender(): Result<String?>
}