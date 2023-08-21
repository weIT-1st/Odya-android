package com.weit.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.weit.domain.usecase.setting.VerifyIgnoringBatteryOptimizationUseCase
import com.weit.domain.usecase.setting.VerifyLocationPermissionUseCase
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

    @Inject
    lateinit var verifyLocationPermissionUseCase: VerifyLocationPermissionUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBottomNavigation()
        viewModel.verifyIgnoringBatteryOptimization(verifyIgnoringBatteryOptimizationUseCase)
        viewModel.verifyNotificationSetting(verifyNotificationSettingUseCase)
        viewModel.verifyLocationPermission(verifyLocationPermissionUseCase)
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

    override fun onStop() {
        super.onStop()
        val serviceIntent = Intent(this, CoordinateForegroundService::class.java)
        startForegroundService(serviceIntent)
    }
}
