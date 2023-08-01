package com.weit.presentation.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth


class CoordinateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val serviceIntent = Intent(context, CoordinateForegroundService::class.java)
                context.startForegroundService(serviceIntent)
            }
        }
    }

}
