package com.weit.data.source

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.weit.data.model.ListResponse
import com.weit.data.model.user.UserContentDTO
import com.weit.data.model.user.UserDTO
import com.weit.data.service.UserService
import com.weit.domain.model.user.User
import com.weit.domain.model.user.UserByNickname
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class UserDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userService: UserService,
) {

    private val Context.dataStore by preferencesDataStore("UserStorePref")

    suspend fun getUser(): UserDTO {
        return userService.getUser()
    }

    suspend fun updateEmail(emailUpdateUser: User) {
        userService.updateEmail(emailUpdateUser)
    }

    suspend fun updatePhoneNumber(phoneNumberUpdateUser: User) {
        userService.updatePhoneNumber(phoneNumberUpdateUser)
    }

    suspend fun updateInformation(informationUpdateUser: User) {
        userService.updateInformation(informationUpdateUser)
    }

    suspend fun updateProfile(profile: MultipartBody.Part): Response<Unit> {
        return userService.updateUserProfile(profile)
    }

    suspend fun setUserId(userId: Long) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    suspend fun getUserId(): Long? =
        context.dataStore.data.map { prefs ->
            prefs[USER_ID_KEY]
        }.first()

    suspend fun getUserByNickname(userByNickname: UserByNickname): ListResponse<UserContentDTO> =
        userService.getUserByNickname(
            size = userByNickname.size,
            lastId = userByNickname.lastId,
            nickname = userByNickname.nickname,
        )

    companion object {
        private const val USER_ID = "USER_ID"
        private val USER_ID_KEY = longPreferencesKey(USER_ID)
    }
}
