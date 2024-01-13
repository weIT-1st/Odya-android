package com.weit.presentation.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.weit.domain.usecase.auth.LoginWithKakaoUseCase
import com.weit.presentation.databinding.ActivitySplashBinding
import com.weit.presentation.ui.MainActivity
import com.weit.presentation.ui.base.BaseActivity
import com.weit.presentation.ui.login.LoginActivity
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding> (
    ActivitySplashBinding::inflate,
) {
    @Inject
    lateinit var loginWithKakaoUseCase: LoginWithKakaoUseCase

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onLoginWithKakako(loginWithKakaoUseCase)
    }

    override fun initCollector() {
        repeatOnStarted(this){
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }


    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleEvent(event: SplashViewModel.Event){
        when(event){
            SplashViewModel.Event.LoginFail -> {moveToLogin()}
            SplashViewModel.Event.LoginSuccess -> {
                moveToMain()
            }
            is SplashViewModel.Event.UserRegistrationRequired -> {}
        }
    }
}
