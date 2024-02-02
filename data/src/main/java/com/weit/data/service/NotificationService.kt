package com.weit.data.service

import com.weit.data.model.user.FcmToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH

interface NotificationService {

    @PATCH("/api/v1/users/fcm-token")
    suspend fun updateFcmToken(
        @Body fcmToken: FcmToken,
    ): Response<Unit>

}
