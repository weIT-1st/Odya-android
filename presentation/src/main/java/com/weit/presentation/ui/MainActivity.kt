package com.weit.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.weit.domain.usecase.image.PickImageUseCase
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

    @Inject
    lateinit var pickImageUseCase: PickImageUseCase



    private val destinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            setBottomNavigationVisibility(destination)
        }

    private val exceptBottomNavigationSet = hashSetOf(
        R.id.postTravelLogFragment,
        R.id.travelFriendFragment,
        R.id.selectPlaceFragment,
    )

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
        navController.addOnDestinationChangedListener(destinationChangedListener)
    }

    private fun setBottomNavigationVisibility(destination: NavDestination) {
        binding.bottomNavigationView.isVisible = exceptBottomNavigationSet.contains(destination.id).not()
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

//    override fun onDestroy() {
//        navController.removeOnDestinationChangedListener(destinationChangedListener)
//        super.onDestroy()
//    }
}
