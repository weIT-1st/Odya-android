package com.weit.presentation.ui

import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.weit.presentation.R
import com.weit.presentation.databinding.ActivityMainBinding
import com.weit.presentation.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBottomNavigation()
        setIgnoringBatteryOptimization()
    }

    private fun setIgnoringBatteryOptimization() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        val packageName = packageName

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, 0)
            }
        }
    }

    private fun setupBottomNavigation() {
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.navigation_main) as NavHostFragment? ?: return
        navController = host.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, CoordinateForegroundService::class.java)
        stopService(serviceIntent)
    }
}
