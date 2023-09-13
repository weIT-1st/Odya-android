package com.weit.domain.usecase.user

import com.weit.domain.model.exception.NicknameNotFoundException
import com.weit.domain.model.user.UserByNickname
import com.weit.domain.model.user.UserByNicknameInfo
import com.weit.domain.model.user.UserContent
import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class GetUserByNicknameUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(nickname: String): Result<UserContent> {
        var size = 10
        var searchNextPage = true
        var result = getUser(size, nickname)

        while (searchNextPage) {
            val info = result.getOrThrow()
            if (info.hasNext && info.content.find { it.nickname == nickname } == null) {
                size += 10
                result = getUser(size, nickname)
            } else {
                searchNextPage = false
            }
        }
        val user = result.getOrThrow().content.find { it.nickname == nickname }
        return if (user == null) {
            Result.failure(NicknameNotFoundException())
        } else {
            Result.success(user)
        }
    }

    private suspend fun getUser(size: Int, nickname: String): Result<UserByNicknameInfo> =
        userRepository.getUserByNickname(UserByNickname(size, null, nickname))
}
