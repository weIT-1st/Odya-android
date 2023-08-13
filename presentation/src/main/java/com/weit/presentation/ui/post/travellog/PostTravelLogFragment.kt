package com.weit.presentation.ui.post.travellog

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentPostTravelLogBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class PostTravelLogFragment : BaseFragment<FragmentPostTravelLogBinding>(
    FragmentPostTravelLogBinding::inflate,
) {

    @Inject
    lateinit var pickImageUseCase: PickImageUseCase

    private val args: PostTravelLogFragmentArgs by navArgs()

    private val viewModel: PostTravelLogViewModel by navGraphViewModels(R.id.post_graph) {
        defaultViewModelProviderFactory
    }

    private val dailyTravelLogAdapter = DailyTravelLogAdapter { action ->
        handleAdapterAction(action)
    }
    private val travelFriendsAdapter = TravelFriendsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.rvPostTravelLogDaily.adapter = dailyTravelLogAdapter
        binding.includePostTravelLogFriends.rvTravelFriends.run {
            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.item_travel_friends_space))
            adapter = travelFriendsAdapter
        }
        viewModel.initViewState(args.followers?.toList())
    }

    override fun initListener() {
        binding.includePostTravelLogFriends.btnTravelFriendsAdd.setOnClickListener {
            viewModel.onEditTravelFriends()
        }
    }

    override fun initCollector() {
        super.initCollector()
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collect { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.changeTravelLogEvent.collectLatest { logs ->
                dailyTravelLogAdapter.submitList(logs)
            }
        }
    }

    private fun handleEvent(event: PostTravelLogViewModel.Event) {
        when (event) {
            is PostTravelLogViewModel.Event.OnEditTravelFriends -> {
                val action = PostTravelLogFragmentDirections.actionPostTravelLogFragmentToTravelFriendFragment(
                    event.travelFriends.toTypedArray(),
                )
                findNavController().navigate(action)
            }
            is PostTravelLogViewModel.Event.OnChangeTravelFriends -> {
                travelFriendsAdapter.submitList(event.friendsSummary)
                if (event.remainingFriendsCount > 0) {
                    binding.includePostTravelLogFriends.tvTravelFriendsCount.text =
                        getString(R.string.post_travel_log_friends_count, event.remainingFriendsCount)
                }
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
