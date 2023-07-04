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

    suspend fun updateEmail(emailUpdateUser: User) {
        userService.updateEmail(
            emailUpdateUser
        )
    }

    suspend fun updatePhoneNumber(phoneNumberUpdateUser: User) {
        userService.updatePhoneNumber(
            phoneNumberUpdateUser
        )
    }

    suspend fun updateInformation(informationUpdateUser: User) {
        userService.updateInformation(
            informationUpdateUser
        )
    }
}