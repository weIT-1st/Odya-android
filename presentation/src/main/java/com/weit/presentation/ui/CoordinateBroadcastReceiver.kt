package com.weit.presentation.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class CoordinateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if(intent.action==Intent.ACTION_BOOT_COMPLETED){
            val serviceIntent = Intent(context, CoordinateForegroundService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}
