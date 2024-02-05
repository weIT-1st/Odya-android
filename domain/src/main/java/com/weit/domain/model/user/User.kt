package com.weit.domain.model.user

interface User {
    val userId: Long
    val email: String?
    val nickname: String
    val phoneNumber: String?
    val gender: String
    val birthday: String
    val socialType: String
    val profile: UserProfile
}
