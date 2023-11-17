package com.weit.presentation.ui.post.datepicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class DatePickerViewModel(initPeriod: TravelPeriod) : ViewModel() {

    private val _period = MutableStateFlow(initPeriod)
    val period: StateFlow<TravelPeriod> get() = _period

    private val _entity = MutableStateFlow(
        DatePickerEntity(0, initPeriod.start.toMillis(), initPeriod.start, CalenderType.START)
    )
    val entity: StateFlow<DatePickerEntity> get() = _entity


    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun onSelectDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        // 현재시간보다 미래가 설정된 경우 현재시간으로 되돋리기
        val selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
        val isFutureDate = isFutureDate(selectedDate, entity.value.type)
        val updatedDate = selectedDate.takeIf { isFutureDate.not() } ?: period.value.end
        viewModelScope.launch {
            // 되돌렸다면 캘린더도 갱신해주기
            if (isFutureDate) {
                _event.emit(Event.OnDateChanged(updatedDate))
            }
            when (entity.value.type) {
                CalenderType.START -> {
                    _period.emit(period.value.copy(start = updatedDate))
                }
                CalenderType.END -> {
                    _period.emit(period.value.copy(end = updatedDate))
                }
            }
        }
    }

    private fun isFutureDate(date: LocalDate, type: CalenderType): Boolean {
        val dateMillis = date.toMillis()
        return when (type) {
            CalenderType.START -> dateMillis > period.value.end.toMillis()
            CalenderType.END -> dateMillis > System.currentTimeMillis()
        }
    }

    fun onChangeCalenderType(type: CalenderType) {
        viewModelScope.launch {
            val date = period.value
            val entity = when (type) {
                CalenderType.START -> {
                    DatePickerEntity(0, date.end.toMillis(), date.start, type)
                }
                CalenderType.END -> {
                    DatePickerEntity(date.start.toMillis(), Calendar.getInstance().timeInMillis, date.end, type)
                }
            }
            _entity.emit(entity)
        }
    }

    fun onDismiss() {
        viewModelScope.launch {
            _event.emit(Event.OnDismiss)
        }
    }

    fun onComplete() {
        viewModelScope.launch {
            _event.emit(Event.OnComplete(period.value))
        }
    }

    private fun LocalDate.toMillis(): Long {
        return atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    sealed class Event {
        object OnDismiss : Event()
        data class OnDateChanged(val date: LocalDate) : Event()
        data class OnComplete(val travelPeriod: TravelPeriod) : Event()
    }

    data class DatePickerEntity(
        val minDateMillis: Long,
        val maxDateMillis: Long,
        val currentDate: LocalDate,
        val type: CalenderType,
    )
}
