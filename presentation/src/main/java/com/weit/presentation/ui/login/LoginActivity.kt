package com.weit.presentation.ui.login

import android.content.Intent
import androidx.activity.viewModels
import com.orhanobut.logger.Logger
import com.weit.presentation.databinding.ActivityLoginBinding
import com.weit.presentation.ui.MainActivity
import com.weit.presentation.ui.base.BaseActivity
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(
    ActivityLoginBinding::inflate,
) {

    private val viewModel: LoginViewModel by viewModels()

    override fun initListener() {
        binding.btnLoginKakao.setOnClickListener {
            viewModel.onLoginWithKakao()
        }
    }

    override fun initCollector() {
        repeatOnStarted(this) {
            viewModel.loginEvent.collectLatest {
                moveToMain()
            }
        }
        repeatOnStarted(this) {
            viewModel.errorEvent.collectLatest { error ->
                Logger.t("MainTest").i(error.message.toString())
            }
        }
    }

    private fun moveToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
