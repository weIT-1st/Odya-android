package com.weit.domain.model.auth

import java.time.LocalDate

data class UserRegistrationInfo(
    val name: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val nickname: String,
    val gender: String,
    val birthday: LocalDate,
)
