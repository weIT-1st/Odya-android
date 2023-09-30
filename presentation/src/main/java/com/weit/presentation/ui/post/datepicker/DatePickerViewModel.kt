package com.weit.presentation.ui.post.datepicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DatePickerViewModel : ViewModel() {

    private val _period = MutableStateFlow(TravelPeriod())
    val period: StateFlow<TravelPeriod> get() = _period

    private val _type = MutableStateFlow(CalenderType.START)
    val type: StateFlow<CalenderType> get() = _type

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun initTravelPeriod(travelPeriod: TravelPeriod) {
        viewModelScope.launch {
            _period.emit(travelPeriod)
        }
    }

    fun onChangeCalenderType(type: CalenderType) {
        viewModelScope.launch {
            _type.emit(type)
        }
    }

    sealed class Event {
        object OnDismiss : Event()
        data class OnComplete(val travelPeriod: TravelPeriod) : Event()
    }
}
