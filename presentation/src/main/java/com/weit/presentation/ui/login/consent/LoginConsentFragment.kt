package com.weit.presentation.ui.login.consent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.weit.presentation.databinding.FragmentLoginConsentBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginConsentFragment : BaseFragment<FragmentLoginConsentBinding>(
    FragmentLoginConsentBinding::inflate
) {
    private val viewModel: LoginConsentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

    }

    override fun initListener() {
        binding.btnLoginStartOdya.setOnClickListener{
            val action =
                LoginConsentFragmentDirections.actionLoginStepZeroFragmentToLoginStepOneFragment()
            it.findNavController().navigate(action)
        }
        
    }

}