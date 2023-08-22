package com.weit.domain.model

enum class InvalidNicknameReason {
    HasSpecialChar,
    TooShortNickname,
    TooLongNickname,
    IsDuplicateNickname,
    GoodNickname,
    UnknownReason
}
