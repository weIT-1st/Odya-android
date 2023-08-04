package com.weit.presentation.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.weit.presentation.databinding.FragmentLoginStepZeroBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.login.user.login.LoginFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginStepZeroFragment : BaseFragment<FragmentLoginStepZeroBinding>(
    FragmentLoginStepZeroBinding::inflate
) {
    private val viewModel: LoginStepZeroViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        settingUi()
    }

    override fun initListener() {
        binding.checkboxLoginConsentApp.setOnClickListener{
            setConsentApp()
        }

        binding.checkboxLoginConsentPrivacy.setOnClickListener {
            setConsentPrivacy()
        }

        binding.checkboxLoginConsentAll.setOnClickListener {
            setConsentAll()
        }

        binding.btnLoginStartOdya.setOnClickListener{
            val action = LoginStepZeroFragmentDirections.actionLoginStepZeroFragmentToLoginStepOneFragment()
            it.findNavController().navigate(action)
        }
    }

    private fun setConsentApp() {
        viewModel.setCheckApp(binding.checkboxLoginConsentApp.isChecked)
    }

    private fun setConsentPrivacy() {
        viewModel.setCheckPrivacy(binding.checkboxLoginConsentPrivacy.isChecked)
    }

    private fun setConsentAll() {
        viewModel.setCheckAll(binding.checkboxLoginConsentAll.isChecked)
    }

    private fun settingUi() {
        binding.btnLoginZeroGoNextStep.visibility = View.INVISIBLE
        binding.btnLoginZeroGoNextStep.isEnabled = false

        viewModel.isConsetApp.observe(viewLifecycleOwner) {
            binding.checkboxLoginConsentApp.isChecked = it
        }

        viewModel.isConsentPrivacy.observe(viewLifecycleOwner) {
            binding.checkboxLoginConsentPrivacy.isChecked = it
        }
        viewModel.isConsentALl.observe(viewLifecycleOwner){
            binding.checkboxLoginConsentAll.isChecked = it
            if(it){
                binding.btnLoginStartOdya.isEnabled = true
            } else if(!it){
                binding.btnLoginStartOdya.isEnabled = false
            }
        }

    }
}