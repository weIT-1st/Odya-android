package com.weit.presentation.ui.post.datepicker

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentDatePickerBinding
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.ui.base.BaseDialogFragment
import com.weit.presentation.ui.util.repeatOnStarted
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/*
    maxDate가 제대로 갱신이 안되는 문제가 있음
    검색해보니 잘 알려진 문제고 구글은 고칠생각이 없음
    handleDatePickerEntity에서 updateDate가 두번 사용되는데 이것도 maxDate가 그나마 정상 동작하기 위한 민간요법임
    우선 설정한 maxDate보다 미래로 설정하는 경우는 ViewModel의 onSelectDate에서 막아놨고 추후 해결방법이 생기는대로 수정 예정
*/
class DatePickerDialogFragment(
    private val travelPeriod: TravelPeriod,
    private val onComplete: (TravelPeriod) -> Unit,
    private val onDismiss: (() -> Unit)? = null,
) : BaseDialogFragment<FragmentDatePickerBinding>(
    FragmentDatePickerBinding::inflate
) {

    private val viewModel: DatePickerViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DatePickerViewModel(travelPeriod) as T
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
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
        val currentDate = entity.currentDate
        binding.dpDatePicker.run {
            updateDate(1980, 1, 1)
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
                dismiss()
            }
            is DatePickerViewModel.Event.OnDateChanged -> {
                val date = event.date
                binding.dpDatePicker.updateDate(date.year, date.monthValue - 1, date.dayOfMonth)
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss?.invoke()
    }
}
