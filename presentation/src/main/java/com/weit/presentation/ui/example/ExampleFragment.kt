package com.weit.presentation.ui.example

import android.Manifest
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.domain.model.exception.RequestDeniedException
import com.weit.presentation.databinding.FragmentExampleBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ExampleFragment : BaseFragment<FragmentExampleBinding>(
    FragmentExampleBinding::inflate,
) {

    private val viewModel: ExampleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.errorEvent.collectLatest { exception ->
                handleError(exception)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.loadImageEvent.collectLatest { bytes ->
                // bytes -> bitmap 변환은 없을거 같아서 따로 usecase로 빼지 않음
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                binding.ivExampleScaled.setImageBitmap(bitmap)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleError(e: Throwable) {
        val readPermission = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES)
        if (e is RequestDeniedException && readPermission.contains(e.permission)) {
            sendSnackBar("이 일을 기억할 것 입니다.")
        } else {
            sendSnackBar(e.message.toString())
        }
    }

    private fun handleEvent(event: ExampleViewModel.Event) {
        when (event) {
            ExampleViewModel.Event.ExistedPlaceIdException -> {
                sendSnackBar("해당 장소는 이미 관심 장소입니다")
            }
            ExampleViewModel.Event.InvalidRequestException -> {
                sendSnackBar("정보를 제대로 입력하십시오")
            }
            ExampleViewModel.Event.InvalidTokenException -> {
                sendSnackBar("로그인을 다시 시도해보십시오")
            }
            ExampleViewModel.Event.NotExistPlaceIdException -> {
                sendSnackBar("해당 장소는 관심 장소 등록되어있지 않습니다.")
            }
            else -> {}
        }
    }
}
