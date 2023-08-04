package com.weit.presentation.ui.login

import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.weit.presentation.databinding.FragmentLoginStepOneBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.login.user.login.LoginFragmentDirections
import com.weit.presentation.ui.login.user.registration.UserRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginStepOneFragment : BaseFragment<FragmentLoginStepOneBinding>(
    FragmentLoginStepOneBinding::inflate
) {

    val viewModel: LoginStepOneViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

    }

    override fun initListener() {
        binding.btnLoginOneGoNextStep.setOnClickListener {
            val action = LoginStepOneFragmentDirections.acteionLoginStepOneFragmentToLoginStepTwoFragment("")
            it.findNavController().navigate(action)
        }

        binding.btnRandomName.setOnClickListener {

        }

        binding.etLoginName.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER){

            }
            true
        }

    }
}