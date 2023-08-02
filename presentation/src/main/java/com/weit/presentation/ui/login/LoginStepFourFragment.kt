package com.weit.presentation.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentLoginStepFourBinding
import com.weit.presentation.ui.base.BaseFragment

class LoginStepFourFragment : BaseFragment<FragmentLoginStepFourBinding>(
    FragmentLoginStepFourBinding::inflate
) {
    private val viewModel: LoginStepFourViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }
}