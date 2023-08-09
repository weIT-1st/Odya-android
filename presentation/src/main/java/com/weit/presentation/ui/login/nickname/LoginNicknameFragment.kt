package com.weit.presentation.ui.login.nickname

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.weit.presentation.databinding.FragmentLoginNicknameBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.login.consent.LoginConsentFragmentDirections
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginNicknameFragment : BaseFragment<FragmentLoginNicknameBinding>(
    FragmentLoginNicknameBinding::inflate
) {

    val viewModel: LoginNicknameViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initListener() {
        binding.btnLoginOneGoNextStep.setOnClickListener {
            val action = LoginNicknameFragmentDirections.actionLoginNicknameFragmentToLoginGenderbirthFragment()
            it.findNavController().navigate(action)
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.event.collectLatest{ event ->
                handleEvent(event)
            }
        }
    }


    private fun handleEvent(event: LoginNicknameViewModel.Event){
        when(event){
            LoginNicknameViewModel.Event.GoodNickname -> {
                sendSnackBar("사용가능한 닉네임입니다.")
            }
            LoginNicknameViewModel.Event.DuplicateNickname -> {
                sendSnackBar("중복된 닉네임 입니다. 다른 닉네임을 입력하세요")
            }
            LoginNicknameViewModel.Event.TooShortNickname -> {
                sendSnackBar("너무 짧은 닉네임 입니다. 닉네임은 최소 2자, 최대 8자, 특숩문자 불가능")
            }
            LoginNicknameViewModel.Event.TooLongNickname -> {
                sendSnackBar("너무 긴 닉네임 입니다. 닉네임은 최소 2자, 최대 8자, 특숩문자 불가능")
            }
        }
    }

}