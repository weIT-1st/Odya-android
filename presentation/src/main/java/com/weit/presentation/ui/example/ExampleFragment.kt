package com.weit.presentation.ui.example

import android.Manifest
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
    }

    private fun handleError(e: Throwable) {
        val readPermission = listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES)
        if (e is RequestDeniedException && readPermission.contains(e.permission)) {
            sendSnackBar("이 일을 기억할 것 입니다.")
        } else {
            sendSnackBar(e.message.toString())
        }
    }
}
