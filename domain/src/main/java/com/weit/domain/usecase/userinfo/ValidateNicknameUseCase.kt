package com.weit.domain.usecase.userinfo

import com.weit.domain.model.NicknameState
import com.weit.domain.repository.auth.AuthRepository
import java.util.regex.Pattern
import javax.inject.Inject

class ValidateNicknameUseCase @Inject constructor(
    private val authRepository: AuthRepository
){


    suspend operator fun invoke(nickname: String): NicknameState {
        val isDuplicate = authRepository.isDuplicateNickname(nickname)
        NicknameState.UnknownReason

        return when {
            hasSpecialChar((nickname)) -> NicknameState.HasSpecialChar
            nickname.length < 2 -> NicknameState.TooShortNickname
            nickname.length > 9 -> NicknameState.TooLongNickname
            isDuplicate.isSuccess -> NicknameState.GoodNickname
            isDuplicate.isFailure -> NicknameState.IsDuplicateNickname
            else -> NicknameState.UnknownReason
        }
    }

    private fun hasSpecialChar(newNickname: String): Boolean {
        return Pattern.matches(REGEX_SPECIALCHAR, newNickname)
    }

    companion object {
        private const val REGEX_SPECIALCHAR = "[!@#\$%^&*(),.?\":{}|<>]"
    }
}
