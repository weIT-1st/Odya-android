package com.weit.presentation.ui.login.consent.device

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.databinding.FragmentLoginConsentDeviceBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginConsentDeviceFragment: BaseFragment<FragmentLoginConsentDeviceBinding>(
    FragmentLoginConsentDeviceBinding::inflate
){
    private val viewModel: LoginConsentDeviceViewModel by viewModels()

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

    private fun consentPrivacyBottomSheetUp(){
//        val action = LoginConsentDeviceFragmentDirections.actionLoginConsentDeviceFragmentToLoginConsentPrivacyDialogFragment()
//        findNavController().navigate(action)
    }

    private fun handleEvent(event: LoginConsentDeviceViewModel.Event){
        when (event){
            LoginConsentDeviceViewModel.Event.ConsentPrivacyBottomSheetUp -> {consentPrivacyBottomSheetUp()}
        }
    }

}
