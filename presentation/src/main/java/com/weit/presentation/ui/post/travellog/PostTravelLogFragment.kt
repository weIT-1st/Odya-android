package com.weit.presentation.ui.post.travellog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.presentation.databinding.FragmentPostTravelLogBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostTravelLogFragment : BaseFragment<FragmentPostTravelLogBinding>(
    FragmentPostTravelLogBinding::inflate,
) {

    @Inject
    lateinit var pickImageUseCase: PickImageUseCase

    private val viewModel: PostTravelLogViewModel by viewModels()

    private val adapter = DailyTravelLogAdapter { action ->
        handleAdapterAction(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.rvPostTravelLogDaily.adapter = adapter
    }

    override fun initCollector() {
        super.initCollector()
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collect { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: PostTravelLogViewModel.Event) {
        when (event) {
            is PostTravelLogViewModel.Event.OnChangeTravelLog -> {
                adapter.submitList(event.logs)
            }
        }
    }

    private fun handleAdapterAction(action: DailyTravelLogAction) {
        when (action) {
            is DailyTravelLogAction.OnDeletePicture -> {
                viewModel.onDeletePicture(action.position, action.imageIndex)
            }
            is DailyTravelLogAction.OnSelectPictureClick -> {
                viewModel.onSelectPictures(action.position, pickImageUseCase)
            }
            is DailyTravelLogAction.OnSelectPlace -> {

            }
            is DailyTravelLogAction.OnDeleteDailyTravelLog -> {
                viewModel.onDeleteDailyTravelLog(action.position)
            }
        }
    }
}
