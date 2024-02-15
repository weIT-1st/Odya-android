package com.weit.data.source

import android.content.Context
import com.weit.data.model.ListResponse
import com.weit.data.model.image.UserImageResponseDTO
import com.weit.data.model.user.FcmToken
import com.weit.data.model.user.SearchUserContentDTO
import com.weit.data.model.user.UserDTO
import com.weit.data.model.user.UserEmailDTO
import com.weit.data.model.user.UserNicknameDTO
import com.weit.data.model.user.UserPhoneNumDTO
import com.weit.data.model.user.UserStatisticsDTO
import com.weit.data.service.UserService
import com.weit.domain.model.user.LifeshotRequestInfo
import com.weit.domain.model.user.SearchUserRequestInfo
import com.weit.domain.model.user.User
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class UserDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userService: UserService,
) {


    suspend fun getUser(): UserDTO {
        return userService.getUser()
    }

    suspend fun updateEmail(emailUpdateUser: UserEmailDTO) {
        userService.updateEmail(emailUpdateUser)
    }

    suspend fun updatePhoneNumber(phoneNumberUpdateUser: UserPhoneNumDTO) {
        userService.updatePhoneNumber(phoneNumberUpdateUser)
    }

    suspend fun updateInformation(nickname: UserNicknameDTO) {
        userService.updateInformation(nickname)
    }

    suspend fun updateProfile(profile: MultipartBody.Part?): Response<Unit> {
        return userService.updateUserProfile(profile)
    }



    suspend fun deleteUser(): Response<Unit> {
        return userService.deleteUser()
    }

    suspend fun searchUser(searchUserRequestInfo: SearchUserRequestInfo): ListResponse<SearchUserContentDTO> {
        return userService.getSearchUsers(
            searchUserRequestInfo.size,
            searchUserRequestInfo.lastId,
            searchUserRequestInfo.nickname
        )
    }

    suspend fun getUserStatistics(userId: Long): UserStatisticsDTO {
        return userService.getUserStatistics(userId)
    }

    suspend fun getUserLifeshots(lifeshotRequestInfo: LifeshotRequestInfo): ListResponse<UserImageResponseDTO> {
        return userService.getUserLifeShot(
            lifeshotRequestInfo.userId,
            lifeshotRequestInfo.size,
            lifeshotRequestInfo.lastId,
        )
    }
}
