package com.weit.presentation.ui.login.onboarding.journal

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.databinding.FragmentLoginOnboardingJournalBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginOnboardingJournalFragment: BaseFragment<FragmentLoginOnboardingJournalBinding>(
    FragmentLoginOnboardingJournalBinding::inflate
) {

    private val viewModel: LoginOnboardingJournalViewModel by viewModels()

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
    private fun moveToNext(){
        val action = LoginOnboardingJournalFragmentDirections.actionLoginOnboardingJournalFragmentToLoginOnboardingRoutineFragment()
        findNavController().navigate(action)
    }

    private fun handelEvent(event: LoginOnboardingJournalViewModel.Event){
        when (event){
            LoginOnboardingJournalViewModel.Event.GoNextStep -> {moveToNext()}
        }
    }
}
