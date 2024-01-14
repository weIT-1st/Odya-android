package com.weit.presentation.ui.login.login

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.domain.usecase.auth.LoginWithKakaoUseCase
import com.weit.presentation.databinding.FragmentLoginBinding
import com.weit.presentation.ui.MainActivity
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate,
) {
    @Inject
    lateinit var loginWithKakaoUseCase: LoginWithKakaoUseCase

    private val viewModel: LoginViewModel by viewModels()
    override fun initListener() {
        binding.btnLoginKakao.setOnClickListener {
            viewModel.onLoginWithKakao(loginWithKakaoUseCase)
        }
    }

    override fun initCollector() {
        repeatOnStarted(this) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun moveToMain() {
        requireActivity().run {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun moveToLogin(){
        val action = LoginFragmentDirections.actionLoginFragmentToLoginOnboardingFragment()
        findNavController().navigate(action)
    }

    private fun handleEvent(event: LoginViewModel.Event) {
        when (event) {
            is LoginViewModel.Event.UserRegistrationRequired -> {moveToLogin()}
            LoginViewModel.Event.LoginFailed -> {
                moveToLogin()
            }
            LoginViewModel.Event.LoginSuccess -> {
                moveToMain()
            }
        }
    }
}
