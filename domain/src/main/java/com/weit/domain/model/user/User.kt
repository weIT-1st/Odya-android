package com.weit.domain.model.user

interface User {
    val userID: Int
    val email: String?
    val nickname: String
    val phoneNumber: String?
    val gender: String
    val birthday: String
    val socialType: String
    val profile: UserProfile
}
