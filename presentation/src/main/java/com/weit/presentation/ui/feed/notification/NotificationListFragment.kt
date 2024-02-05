package com.weit.presentation.ui.feed.notification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.databinding.FragmentNotificationListBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class NotificationListFragment : BaseFragment<FragmentNotificationListBinding>(
    FragmentNotificationListBinding::inflate,
) {

    private val viewModel: NotificationListViewModel by viewModels()
    private val notificationListAdapter = NotificationListAdapter { action ->
        handleAdapterAction(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initData()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvNotification.run{
            adapter = notificationListAdapter
        }
    }
    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.notifications.collectLatest { list ->
                notificationListAdapter.submitList(list)
            }
        }
    }

    private fun handleAdapterAction(action: NotificationAction) {
        when (action) {
            is NotificationAction.OnClickFeed -> {
                val direction = NotificationListFragmentDirections.actionNotificationListFragmentToFragmentFeedDetail(action.feedId,action.userName)
                findNavController().navigate(direction)
            }

            is NotificationAction.OnClickTravelJournal -> {
               val direction = NotificationListFragmentDirections.actionNotificationListFragmentToFragmentTravelJournal(action.journalId)
                findNavController().navigate(direction)
            }
            is NotificationAction.OnClickOtherProfile -> {
                val direction = NotificationListFragmentDirections.actionNotificationListFragmentToOtherProfileFragment(action.userName)
                findNavController().navigate(direction)
            }
            else -> {}
        }
    }

    override fun onDestroyView() {
        binding.rvNotification.adapter = null
        super.onDestroyView()
    }

}
