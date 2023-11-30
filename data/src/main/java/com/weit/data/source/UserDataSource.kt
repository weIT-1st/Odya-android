package com.weit.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.weit.data.model.ListResponse
import com.weit.data.model.user.SearchUserContentDTO
import com.weit.data.model.user.UserDTO
import com.weit.data.service.UserService
import com.weit.domain.model.user.SearchUserRequestInfo
import com.weit.domain.model.user.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class UserDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userService: UserService,
) {


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

}
