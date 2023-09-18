package com.weit.presentation.ui.login.consent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.weit.presentation.databinding.BottomSheetLoginConsentBinding
import com.weit.presentation.ui.login.preview.LoginPreviewThirdFragmentDirections
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginConsentDialogFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetLoginConsentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginConsentDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetLoginConsentBinding.inflate(inflater, container, false)
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

    private fun handleEvent(event: LoginConsentDialogViewModel.Event) {
        when (event) {
            is LoginConsentDialogViewModel.Event.OnAgreeSuccess -> {
                dismiss()
                val action = LoginPreviewThirdFragmentDirections.actionLoginPreviewThirdFragmentToLoginNicknameFragment()
                findNavController().navigate(action)
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
