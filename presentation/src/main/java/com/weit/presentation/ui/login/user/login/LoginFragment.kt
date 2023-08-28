package com.weit.presentation.ui.login.user.login

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.domain.usecase.auth.LoginWithKakaoUseCase
import com.weit.presentation.databinding.FragmentLoginBinding
import com.weit.presentation.ui.MainActivity
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.placereview.edit.EditPlaceReviewFragment
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
    // 서울시청으로 테스트 장소 Id 세팅
    private var editPlaceReviewFragment: EditPlaceReviewFragment? = EditPlaceReviewFragment("ChIJzz_R5fKifDURJSTIFbslRp0")
    override fun initListener() {
        binding.btnLoginKakao.setOnClickListener {
            viewModel.onLoginWithKakao(loginWithKakaoUseCase)
        }
        binding.btnToMain.setOnClickListener {
            moveToMain()
        }

        binding.btnEditReview.setOnClickListener {
            editReviewDialog()

            binding.btnToLoginStep.setOnClickListener {
                val action = LoginFragmentDirections.actionLoginFragmentToLoginContentFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun initCollector() {
        repeatOnStarted(this) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    override fun onDestroyView() {
        editPlaceReviewFragment = null
        super.onDestroyView()
    }

    private fun handleEvent(event: LoginViewModel.Event) {
        when (event) {
            LoginViewModel.Event.LoginFailed -> {
                // TODO 그냥 실패 시 에러 처리 필요
            }
            is LoginViewModel.Event.UserRegistrationRequired -> {
                findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToUserRegistrationFragment(
                        username = event.username,
                    ),
                )
            }
            LoginViewModel.Event.LoginSuccess -> {
                moveToMain()
            }
        }
    }

    private fun moveToMain() {
        requireActivity().run {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun editReviewDialog() {
        if (!(editPlaceReviewFragment!!.dialog != null && editPlaceReviewFragment!!.dialog!!.isShowing && !editPlaceReviewFragment!!.isRemoving)) {
            editPlaceReviewFragment!!.show(childFragmentManager, "EditReview")
        }
    }
}
