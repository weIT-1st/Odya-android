package com.weit.presentation.ui.login.consent.privacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.weit.presentation.databinding.BottomSheetLoginConsentPrivacyBinding
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginConsentPrivacyDialogFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetLoginConsentPrivacyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginConsentPrivacyDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetLoginConsentPrivacyBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        initCollector()
        return binding.root
    }

    private fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.termTitle.collectLatest { title ->
                binding.tvTermsTitle.text = title
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.termContent.collectLatest { content ->
                binding.tvTermsContent.text = content
            }
        }
    }

    private fun moveToInputName(){
        val action = LoginConsentPrivacyDialogFragmentDirections.actionLoginConsentDialogFragmentToLoginNicknameFragment()
        findNavController().navigate(action)
    }

    private fun handleEvent(event: LoginConsentPrivacyDialogViewModel.Event) {
        when (event) {
            is LoginConsentPrivacyDialogViewModel.Event.OnAgreeSuccess -> {
                dismiss()
                moveToInputName()
            }
            else -> {}
        }
    }

    private fun sendSnackBar(
        message: String,
        length: Int = Snackbar.LENGTH_SHORT,
        anchorView: View? = null,
    ) {
        Snackbar.make(
            binding.root,
            message,
            length,
        ).apply {
            if (anchorView != null) {
                this.anchorView = anchorView
            }
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val TAG = "LoginConsentDialogFragment"
    }
}
