package com.weit.domain.model.user

interface User {
    val userId: Long
    var email: String?
    var nickname: String
    var phoneNumber: String?
    val gender: String
    val birthday: String
    val socialType: String
    val profile: UserProfile
}
