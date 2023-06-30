package com.weit.presentation.ui

import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.weit.presentation.databinding.ActivityMainBinding
import com.weit.presentation.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate){
    override fun preLoad(){
        installSplashScreen()
    }
    val content: View = findViewById(android.R.id.content)
}
