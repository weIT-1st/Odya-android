package com.weit.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.weit.domain.usecase.setting.VerifyIgnoringBatteryOptimizationUseCase
import com.weit.domain.usecase.setting.VerifyNotificationSettingUseCase
import com.weit.presentation.R
import com.weit.presentation.databinding.ActivityMainBinding
import com.weit.presentation.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController

    @Inject
    lateinit var verifyIgnoringBatteryOptimizationUseCase: VerifyIgnoringBatteryOptimizationUseCase

    @Inject
    lateinit var verifyNotificationSettingUseCase: VerifyNotificationSettingUseCase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBottomNavigation()

        viewModel.verifyIgnoringBatteryOptimization(verifyIgnoringBatteryOptimizationUseCase)
        viewModel.verifyNotificationSetting(verifyNotificationSettingUseCase)
        checkLocationPermission()
    }

    private fun setupBottomNavigation() {
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.navigation_main) as NavHostFragment? ?: return
        navController = host.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun checkLocationPermission() {
        val finePermission = Manifest.permission.ACCESS_FINE_LOCATION
        val granted = PackageManager.PERMISSION_GRANTED

        if (ContextCompat.checkSelfPermission(this, finePermission) != granted) {
            // 이 코드 때문에 mainactivity에서 작업하는데 더 좋은 방법이 없나...
            ActivityCompat.requestPermissions(this, arrayOf(finePermission), 101)
        }
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, CoordinateForegroundService::class.java)
        stopService(serviceIntent)
    }

    override fun onStop() {
        super.onStop()
        val serviceIntent = Intent(this, CoordinateForegroundService::class.java)
        startForegroundService(serviceIntent)
    }
}
