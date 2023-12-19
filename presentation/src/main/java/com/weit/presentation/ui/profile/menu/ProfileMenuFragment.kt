package com.weit.presentation.ui.profile.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.presentation.databinding.BottomSheetFeedMyMenuBinding
import com.weit.presentation.databinding.BottomSheetMyProfileMenuBinding
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class ProfileMenuFragment(private val profileImage: (String) -> Unit) :
    BottomSheetDialogFragment() {
    private var _binding: BottomSheetMyProfileMenuBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var pickImageUseCase: PickImageUseCase

    private val viewModel: ProfileMenuViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetMyProfileMenuBinding.inflate(inflater, container, false)
        initCollector()
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvProfileImageSelect.setOnClickListener {
            viewModel.onUpdateProfileImage(pickImageUseCase)
        }
        binding.tvProfileImageNone.setOnClickListener {
            viewModel.onUpdateProfileImageNone()
        }
        binding.tvMyProfileCancel.setOnClickListener {
            dismiss()
        }
    }

    fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: ProfileMenuViewModel.Event) {
        when (event) {
            is ProfileMenuViewModel.Event.OnChangeProfileImageSuccess -> {
                dismiss()
            }

            is ProfileMenuViewModel.Event.OnChangeProfileNoneSuccess -> {
                dismiss()
            }

            else -> {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ProfileMenuFragment"
    }
}
