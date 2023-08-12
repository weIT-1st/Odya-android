package com.weit.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.weit.domain.usecase.setting.VerifyIgnoringBatteryOptimizationUseCase
import com.weit.presentation.R
import com.weit.presentation.databinding.ActivityMainBinding
import com.weit.presentation.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val vm: MainViewModel by viewModels()
    private lateinit var navController: NavController

    @Inject
    lateinit var verifyIgnoringBatteryOptimizationUseCase: VerifyIgnoringBatteryOptimizationUseCase

    private val destinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            setBottomNavigationVisibility(destination)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBottomNavigation()
        vm.verifyIgnoringBatteryOptimization(verifyIgnoringBatteryOptimizationUseCase)
    }

    private fun setupBottomNavigation() {
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.navigation_main) as NavHostFragment? ?: return
        navController = host.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(destinationChangedListener)
    }

    private fun setBottomNavigationVisibility(destination: NavDestination) {
        val isVisible = when (destination.id) {
            R.id.postTravelLogFragment -> false
            else -> true
        }
        binding.bottomNavigationView.isVisible = isVisible
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, CoordinateForegroundService::class.java)
        stopService(serviceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(destinationChangedListener)
    }
}
