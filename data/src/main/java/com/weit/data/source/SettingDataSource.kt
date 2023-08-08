package com.weit.data.source

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.weit.data.db.CoordinateDatabase
import com.weit.data.model.Coordinate
import com.weit.domain.model.CoordinateTimeInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class SettingDataSource @Inject constructor(
    private val pm : PowerManager,
    private val context : Context
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
