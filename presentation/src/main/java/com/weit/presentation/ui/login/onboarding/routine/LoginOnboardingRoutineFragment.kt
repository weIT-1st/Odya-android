package com.weit.presentation.ui.login.onboarding.routine

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.databinding.FragmentLoginOnboardingRoutineBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import kotlinx.coroutines.flow.collectLatest

class LoginOnboardingRoutineFragment: BaseFragment<FragmentLoginOnboardingRoutineBinding> (
    FragmentLoginOnboardingRoutineBinding::inflate
) {
    private val viewModel: LoginOnboardingRoutineViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.event.collectLatest { event ->
                handelEvent(event)
            }
        }
    }

    private fun moveToConsent(){
        val action = LoginOnboardingRoutineFragmentDirections.actionLoginOnboardingRoutineFragmentToLoginConsentDeviceFragment()
        findNavController().navigate(action)
    }

    private fun handelEvent(event: LoginOnboardingRoutineViewModel.Event){
        when(event){
            LoginOnboardingRoutineViewModel.Event.GoConsent -> {moveToConsent()}
        }
    }
}
