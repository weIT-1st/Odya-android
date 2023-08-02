package com.weit.presentation.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentLoginStepThreeBinding
import com.weit.presentation.ui.base.BaseFragment


class LoginStepThreeFragment : BaseFragment<FragmentLoginStepThreeBinding>(
    FragmentLoginStepThreeBinding::inflate
) {
    private val viewModel: LoginStepThreeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }
}