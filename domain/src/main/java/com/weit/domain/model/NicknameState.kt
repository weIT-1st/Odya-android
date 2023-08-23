package com.weit.domain.model

enum class NicknameState {
    HasSpecialChar,
    TooShortNickname,
    TooLongNickname,
    IsDuplicateNickname,
    GoodNickname,
    UnknownReason,
}
