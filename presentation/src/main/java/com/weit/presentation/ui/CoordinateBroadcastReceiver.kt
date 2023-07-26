package com.weit.presentation.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class CoordinateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if(intent.action==Intent.ACTION_BOOT_COMPLETED){
            StringBuilder().apply {
                append("Action: ${intent.action}\n")
                append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
                toString().also { log ->
                    Log.d("MyBroadcastReceiver", log)
                    Toast.makeText(context, log, Toast.LENGTH_LONG).show()
                }
            }
            val serviceIntent = Intent(context, CoordinateForegroundService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}