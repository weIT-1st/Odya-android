package com.weit.presentation.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.weit.domain.usecase.coordinate.InsertCoordinateUseCase
import com.weit.presentation.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CoordinateForegroundService : Service() {

    @Inject
    lateinit var insertCoordinateUseCase: InsertCoordinateUseCase

    private var traceJob: Job = Job()
    private var insertJob: Job = Job()

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "CoordinateChannel"
        private const val MIN_TIME_BETWEEN_UPDATES: Long = 5000
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 2f
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_MUTABLE,
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Odya")
            .setContentText("좌표 수집 중입니다")
            .setSmallIcon(R.drawable.ic_splash_logo)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        traceJob = CoroutineScope(Dispatchers.Main).launch {
            val result = trackLocation(this@CoordinateForegroundService)
            result.collect {
                insertCoordinateDB(it)
            }
        }
        return START_STICKY
    }

    private fun trackLocation(context: Context): Flow<Location> = callbackFlow {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trySend(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener,
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        awaitClose { locationManager.removeUpdates(locationListener) }
    }

    private fun insertCoordinateDB(location: Location) {
        insertJob = CoroutineScope(Dispatchers.IO).launch {
            insertCoordinateUseCase(location.latitude.toFloat(), location.longitude.toFloat())
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Odya Coordinate Channel",
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        traceJob.cancel()
        insertJob.cancel()
    }
}
