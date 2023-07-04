package com.weit.presentation.ui.splash

import android.content.Intent
import androidx.activity.viewModels
import com.weit.presentation.databinding.ActivitySplashBinding
import com.weit.presentation.ui.base.BaseActivity
import com.weit.presentation.ui.login.LoginActivity
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding> (
    ActivitySplashBinding::inflate
        ){

    private val viewModel: SplashViewModel by viewModels()

    override fun initCollector() {
        repeatOnStarted(this){
            viewModel.splashEvent.collectLatest {
                moveToLogin()
            }
        }
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}

