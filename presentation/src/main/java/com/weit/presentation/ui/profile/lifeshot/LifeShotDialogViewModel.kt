package com.weit.presentation.ui.profile.lifeshot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.orhanobut.logger.Logger
import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.usecase.community.comment.DeleteCommentsUseCase
import com.weit.domain.usecase.community.comment.GetCommentsUseCase
import com.weit.domain.usecase.community.comment.RegisterCommentsUseCase
import com.weit.domain.usecase.community.comment.UpdateCommentsUseCase
import com.weit.domain.usecase.image.SetLifeShotUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.model.profile.lifeshot.SelectLifeShotImageDTO
import com.weit.presentation.ui.feed.detail.CommentDialogViewModel
import com.weit.presentation.ui.login.inputuserinfo.LoginInputUserInfoViewModel
import com.weit.presentation.ui.post.selectplace.SelectPlaceViewModel
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import com.weit.presentation.ui.util.toMillis
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar

class LifeShotDialogViewModel @AssistedInject constructor(
    private val setLifeShotUseCase: SetLifeShotUseCase,
    @Assisted private val imageInfo: SelectLifeShotImageDTO?,
    @Assisted private val placeName: String?,
) : ViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private val _lifeshotImage = MutableStateFlow<String?>(null)
    val lifeshotImage: StateFlow<String?> get() = _lifeshotImage

//    private var selectPlaceName : String = ""

    @AssistedFactory
    interface LifeShotFactory {
        fun create(imageInfo: SelectLifeShotImageDTO?,placeName: String?): LifeShotDialogViewModel
    }

    init{
        viewModelScope.launch {
            _lifeshotImage.emit(imageInfo?.imageUri)
        }
    }

//    fun setLifeShotPlace(placeName: String?) {
//        selectPlaceName = placeName.toString()
//    }

     fun registerLifeShot() {
        viewModelScope.launch {
            val result = setLifeShotUseCase(imageInfo?.imageId?:0,placeName.toString())
            if(result.isSuccess){
                _event.emit(LifeShotDialogViewModel.Event.OnComplete)
            }else{
                Logger.t("lifeShotJob").i("${result.exceptionOrNull()}")

            }
        }
    }


    sealed class Event {
        object OnComplete : Event()

    }

    companion object {
        fun provideFactory(
            assistedFactory: LifeShotDialogViewModel.LifeShotFactory,
            imageInfo: SelectLifeShotImageDTO?,
            placeName: String?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(imageInfo,placeName) as T
            }
        }
    }
}
