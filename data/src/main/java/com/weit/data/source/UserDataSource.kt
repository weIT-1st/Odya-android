package com.weit.data.source

import com.weit.data.model.user.UserDTO
import com.weit.data.service.UserService
import com.weit.domain.model.user.User
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val userService: UserService
) {

    suspend fun getUser() : UserDTO {
        return userService.getUser()
    }

    suspend fun updateEmail(newEmail: String) {
        userService.updateEmail(
            email = newEmail
        )
    }

    suspend fun updatePhoneNumber(newPhonenumber: String) {
        userService.updatePhoneNumber(
            phoneNumber = newPhonenumber
        )
    }

    suspend fun updateInformation(newNickname: String) {
        userService.updateInformation(
            nickname = newNickname
        )
    }
}