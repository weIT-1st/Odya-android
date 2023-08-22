package com.weit.domain.usecase.userinfo

import com.weit.domain.model.InvalidNicknameReason
import com.weit.domain.repository.auth.AuthRepository
import java.util.regex.Pattern
import javax.inject.Inject

class ValidateNicknameUseCase @Inject constructor(
    private val authRepository: AuthRepository
){


    suspend operator fun invoke(nickname: String): InvalidNicknameReason {
        val isDuplicate = authRepository.isDuplicateNickname(nickname)
        var invalidNicknameReason: InvalidNicknameReason = InvalidNicknameReason.UnknownReason

        if (hasSpecialChar(nickname)){
            invalidNicknameReason = InvalidNicknameReason.HasSpecialChar
        } else {
            if (nickname.length < 2) {
                invalidNicknameReason = InvalidNicknameReason.TooShortNickname
            } else if (nickname.length > 9){
                invalidNicknameReason = InvalidNicknameReason.TooLongNickname
            } else {
                if (isDuplicate.isSuccess){
                    invalidNicknameReason = InvalidNicknameReason.GoodNickname
                } else {
                    invalidNicknameReason = InvalidNicknameReason.IsDuplicateNickname
                }
            }
        }

        return invalidNicknameReason
    }

    private fun hasSpecialChar(newNickname: String): Boolean {
        return Pattern.matches(REGEX_SPECIALCHAR, newNickname)
    }

    companion object {
        private const val REGEX_SPECIALCHAR = "[!@#\$%^&*(),.?\":{}|<>]"
    }
}
