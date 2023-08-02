package com.weit.presentation.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentLoginMainBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginMainFragment : BaseFragment<FragmentLoginMainBinding>(
    FragmentLoginMainBinding::inflate
) {
    private val viewModel: LoginMainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        setLoginStepViewPager()
        binding.btnLoginGoBackStep
        binding.btnLoginGoNextStep
    }

    private fun setLoginStepViewPager(){
        binding.viewpagerLogin.adapter = LoginStepAdapter(this)
        binding.dotsIndicatorLogin.attachTo(binding.viewpagerLogin)
    }
}