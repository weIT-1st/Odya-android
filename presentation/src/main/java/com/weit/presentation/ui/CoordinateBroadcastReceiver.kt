package com.weit.presentation.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.weit.domain.usecase.auth.verifyCurrentUserUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class CoordinateBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var verifyCurrentUserUseCase: verifyCurrentUserUseCase
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            runBlocking {
                if (verifyCurrentUserUseCase()) {
                    val serviceIntent = Intent(context, CoordinateForegroundService::class.java)
                    context.startForegroundService(serviceIntent)
                }
            }
        }
    }

}
