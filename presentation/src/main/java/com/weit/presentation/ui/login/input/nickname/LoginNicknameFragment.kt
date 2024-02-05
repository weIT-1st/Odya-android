package com.weit.presentation.ui.login.input.nickname

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.databinding.FragmentLoginInputNicknameBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginNicknameFragment : BaseFragment<FragmentLoginInputNicknameBinding>(
    FragmentLoginInputNicknameBinding::inflate,
) {

    val viewModel: LoginNicknameViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initListener() {
        deleteEditTextAll()
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun moveToInitUserInfo(){
        val action =
            LoginNicknameFragmentDirections.actionLoginNicknameFragmentToLoginInputUserInfoFragment2()
        findNavController().navigate(action)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun deleteEditTextAll(){
        val etLoginInputName = binding.etLoginInputName

        etLoginInputName.setOnTouchListener(
            View.OnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.x >= (etLoginInputName.right - etLoginInputName.compoundDrawables[2].bounds.width())) {
                        etLoginInputName.text = null
                    }
                }
                false
            },
        )
    }

    private fun handleEvent(event: LoginNicknameViewModel.Event) {
        when (event) {
            LoginNicknameViewModel.Event.GoodNickname -> {
                sendSnackBar("사용 가능한 닉네임 입니다.")
            }
            LoginNicknameViewModel.Event.DuplicateNickname -> {
                sendSnackBar("중복된 닉네임 입니다. 다른 닉네임을 입력하세요")
            }
            LoginNicknameViewModel.Event.TooShortNickname -> {
                sendSnackBar("너무 짧은 닉네임 입니다. 닉네임은 최소 2자, 최대 8자, 특수문자 불가능")
            }
            LoginNicknameViewModel.Event.TooLongNickname -> {
                sendSnackBar("너무 긴 닉네임 입니다. 닉네임 최소 2자, 최대 8자, 특수문자 불가능")
            }
            LoginNicknameViewModel.Event.HasSpecialChar -> {
                sendSnackBar("특수문자가 포함되어 있는 닉네임 입니다. 닉네임 최소 2자, 최대 8자, 특수문자 불가능")
            }
            LoginNicknameViewModel.Event.NullDefaultNickname -> {
                sendSnackBar("새로운 닉네임을 입력하세요. 닉네임 최소 2자, 최대 8자, 특수문자 불가능")
            }
            LoginNicknameViewModel.Event.UnknownNickname -> {
                sendSnackBar("새로운 닉네임을 입력하세요. 닉네임 최소 2자, 최대 8자, 특수문자 불가능")
            }
            LoginNicknameViewModel.Event.SuccessSetNickname -> {
                moveToInitUserInfo()
            }
        }
    }
}