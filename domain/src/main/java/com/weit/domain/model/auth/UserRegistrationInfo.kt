package com.weit.domain.model.auth

data class UserRegistrationInfo(
    val name: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val nickname: String,
    val gender: String,
    val birthday: List<Int>,
)
