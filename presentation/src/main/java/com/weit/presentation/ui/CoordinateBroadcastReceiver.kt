package com.weit.presentation.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.weit.domain.usecase.auth.VerifyCurrentUserUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CoordinateBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var verifyCurrentUserUseCase: VerifyCurrentUserUseCase
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && verifyCurrentUserUseCase()) {
            val serviceIntent = Intent(context, CoordinateForegroundService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}
