package com.weit.data.source

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import javax.inject.Inject

class SettingDataSource @Inject constructor(
    private val pm: PowerManager,
    private val context: Context,
) {
    fun setIgnoringBatteryOptimization() {
        val packageName = context.packageName
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            context.startActivity(intent)
        }
    }
}
