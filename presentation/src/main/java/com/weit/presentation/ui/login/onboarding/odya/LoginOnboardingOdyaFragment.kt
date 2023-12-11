package com.weit.presentation.ui.login.onboarding.odya

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.databinding.FragmentLoginOnboardingOdyaBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import kotlinx.coroutines.flow.collectLatest

class LoginOnboardingOdyaFragment: BaseFragment<FragmentLoginOnboardingOdyaBinding> (
    FragmentLoginOnboardingOdyaBinding::inflate
) {

    private val viewModel: LoginOnboardingOdyaViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun moveToNext() {
        val action = LoginOnboardingOdyaFragmentDirections.actionLoginOnboardingOdyaFragmentToLoginOnboardingJournalFragment()
        findNavController().navigate(action)
    }

    private fun handleEvent(event: LoginOnboardingOdyaViewModel.Event){
        when (event) {
            LoginOnboardingOdyaViewModel.Event.GoNextStep -> {
                moveToNext()
            }
        }
    }
}
