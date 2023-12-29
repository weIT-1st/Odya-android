package com.weit.presentation.ui.login.loading

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.databinding.FragmentLoginLoadingBinding
import com.weit.presentation.ui.MainActivity
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginLoadingFragment: BaseFragment<FragmentLoginLoadingBinding>(
    FragmentLoginLoadingBinding::inflate
) {
    val viewModel: LoginLoadingViewModel by viewModels()

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.event.collectLatest { event ->
                handelEvent(event)
            }
        }
    }

    private fun moveToMain() {
        requireActivity().run {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun moveToInput(){
        val action = LoginLoadingFragmentDirections.actionLoginLoadingFragmentToLoginNicknameFragment()
        findNavController().navigate(action)
    }

    private fun moveToLogin(){
        val action = LoginLoadingFragmentDirections.actionLoginLoadingFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun handelEvent(event: LoginLoadingViewModel.Event) {
        when(event){
            LoginLoadingViewModel.Event.SuccessRegister -> {
                moveToMain()
            }
            LoginLoadingViewModel.Event.DuplicatedSomethingException -> {
                sendSnackBar("이미 계정을 가지고 있는 유저 입니다. 로그인 진행해주세요")
                moveToLogin()
            }
            LoginLoadingViewModel.Event.InvalidRequestException -> {
                sendSnackBar("입력하신 정보에 문제가 있습니다. 다시 입력하세요")
                moveToInput()
            }
            LoginLoadingViewModel.Event.UnKnownException -> {
                sendSnackBar("알수 없는 에러가 있습니다. 앱을 다시 실행하세요")
            }
        }
    }
}
