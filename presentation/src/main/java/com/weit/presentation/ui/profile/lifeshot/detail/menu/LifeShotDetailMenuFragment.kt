package com.weit.presentation.ui.profile.lifeshot.detail.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orhanobut.logger.Logger
import com.weit.presentation.databinding.BottomSheetLifeshotMenuBinding
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class LifeShotDetailMenuFragment(
    private val imageId: Long,
    private val onComplete: (Unit) -> Unit,
) :
    BottomSheetDialogFragment() {
    private var _binding: BottomSheetLifeshotMenuBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var viewModelFactory: LifeShotDetailMenuViewModel.LifeShotFactory

    private val viewModel: LifeShotDetailMenuViewModel by viewModels {
        LifeShotDetailMenuViewModel.provideFactory(viewModelFactory, imageId)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetLifeshotMenuBinding.inflate(inflater, container, false)
        initCollector()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvLifeshotDelete.setOnClickListener {
            viewModel.deleteLifeShot()
        }
        binding.tvLifeshotCancel.setOnClickListener {
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

    private fun handleEvent(event: LifeShotDetailMenuViewModel.Event) {
        when (event) {
            is LifeShotDetailMenuViewModel.Event.OnDeleteLifeShotSuccess -> {
                dismiss()
                onComplete(Unit)
            }
            else -> {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "LifeShotDetailMenuFragment"
    }
}
