package com.weit.presentation.ui.post.datepicker

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentDatePickerBinding
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.ui.base.BaseDialogFragment
import com.weit.presentation.ui.util.repeatOnStarted
import kotlinx.coroutines.flow.collectLatest

class DatePickerDialogFragment(
    private val travelPeriod: TravelPeriod,
    private val onComplete: (TravelPeriod) -> Unit,
) : BaseDialogFragment<FragmentDatePickerBinding>(
    FragmentDatePickerBinding::inflate
) {

    private val viewModel: DatePickerViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dpDatePicker.minDate = System.currentTimeMillis()
        viewModel.initTravelPeriod(travelPeriod)
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.period.collectLatest { period ->

            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.type.collectLatest { type ->

            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collect { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: DatePickerViewModel.Event) {
        when (event) {
            is DatePickerViewModel.Event.OnComplete -> {
                onComplete(event.travelPeriod)
                dismiss()
            }
            DatePickerViewModel.Event.OnDismiss -> {
                dismiss()
            }
        }
    }
}
