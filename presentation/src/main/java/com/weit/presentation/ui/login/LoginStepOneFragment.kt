package com.weit.presentation.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentLoginStepOneBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginStepOneFragment : BaseFragment<FragmentLoginStepOneBinding>(
    FragmentLoginStepOneBinding::inflate
) {
    private val viewModel: LoginStepOneViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }
}