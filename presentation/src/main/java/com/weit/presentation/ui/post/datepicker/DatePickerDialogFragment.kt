package com.weit.presentation.ui.post.datepicker

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentDatePickerBinding
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.ui.base.BaseDialogFragment
import com.weit.presentation.ui.util.repeatOnStarted
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class DatePickerDialogFragment(
    private val travelPeriod: TravelPeriod,
    private val onComplete: (TravelPeriod) -> Unit,
) : BaseDialogFragment<FragmentDatePickerBinding>(
    FragmentDatePickerBinding::inflate
) {

    private val viewModel: DatePickerViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        viewModel.initTravelPeriod(travelPeriod)
    }

    override fun initListener() {
        binding.dpDatePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            viewModel.onSelectDate(year, monthOfYear, dayOfMonth)
        }
        binding.includeDatePickerStart.root.setOnClickListener {
            viewModel.onChangeCalenderType(CalenderType.START)
        }
        binding.includeDatePickerEnd.root.setOnClickListener {
            viewModel.onChangeCalenderType(CalenderType.END)
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.period.collectLatest { period ->
                handlePeriod(period)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.entity.collectLatest { entity ->
                handleDatePickerEntity(entity)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collect { event ->
                handleEvent(event)
            }
        }
    }

    private fun handlePeriod(period: TravelPeriod) {
        binding.includeDatePickerStart.run {
            tvYear.text = getString(R.string.date_picker_year, period.start.year)
            tvDate.text = period.start.toDateString()
        }
        binding.includeDatePickerEnd.run {
            tvYear.text = getString(R.string.date_picker_year, period.end.year)
            tvDate.text = period.end.toDateString()
        }
    }

    private fun handleDatePickerEntity(entity: DatePickerViewModel.DatePickerEntity) {
        binding.dpDatePicker.run {
            minDate = entity.minDateMillis
            maxDate = entity.maxDateMillis
            val currentDate = entity.currentDate
            updateDate(currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth)
        }
        handleCalendarType(entity.type)
    }

    private fun handleCalendarType(type: CalenderType) {
        val startTextColor = getTextColor(type == CalenderType.START)
        val startBackground = getBackgroundTint(type == CalenderType.START)
        val endTextColor = getTextColor(type == CalenderType.END)
        val endBackground = getBackgroundTint(type == CalenderType.END)
        binding.includeDatePickerStart.run {
            tvDate.setTextColor(startTextColor)
            tvYear.setTextColor(startTextColor)
            root.backgroundTintList = startBackground
        }
        binding.includeDatePickerEnd.run {
            tvDate.setTextColor(endTextColor)
            tvYear.setTextColor(endTextColor)
            root.backgroundTintList = endBackground
        }
    }

    private fun getTextColor(isSelected: Boolean): Int {
        val color = if (isSelected) {
            R.color.background_normal
        } else {
            R.color.label_alternative
        }
        return resources.getColor(color, null)
    }

    private fun getBackgroundTint(isSelected: Boolean): ColorStateList? {
        val color = if (isSelected) {
            R.color.primary
        } else {
            R.color.background_normal
        }
        return ContextCompat.getColorStateList(requireContext(), color)
    }

    private fun handleEvent(event: DatePickerViewModel.Event) {
        when (event) {
            is DatePickerViewModel.Event.OnComplete -> {
                onComplete(event.travelPeriod)
                dismiss()
            }
            DatePickerViewModel.Event.OnDismiss -> {
                onComplete(travelPeriod)
                dismiss()
            }
        }
    }

    private fun LocalDate.toDateString(): String {
        return getString(
            R.string.date_picker_date,
            monthValue,
            dayOfMonth,
            dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        )
    }
}
