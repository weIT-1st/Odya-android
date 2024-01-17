package com.weit.presentation.ui.login.input.friend

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.R
import com.weit.presentation.databinding.DialogLoginFriendBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginFriendFragment: BaseFragment<DialogLoginFriendBinding>(
    DialogLoginFriendBinding::inflate
) {
    val viewModel: LoginFriendViewModel by viewModels()

    private val mayKnowFriendAdapter = MayKnowFriendAdapter{ viewModel.createFollowState(it) }
    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextFriends()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initMayKnowFriendRV()
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.mayKnowFriends.collectLatest { list ->
                mayKnowFriendAdapter.submitList(list)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    override fun onDestroyView() {
        binding.rvLoginFriend.removeOnScrollListener(infinityScrollListener)
        binding.rvLoginFriend.adapter = null
        super.onDestroyView()
    }

    private fun initMayKnowFriendRV() {
        binding.rvLoginFriend.apply {
            adapter = mayKnowFriendAdapter
//            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.item_may_know_friend_space))
            addOnScrollListener(infinityScrollListener)
        }
    }

    private fun handleEvent(event: LoginFriendViewModel.Event) {
        when (event) {
            LoginFriendViewModel.Event.MoveToBack -> {
                findNavController().popBackStack()
            }
            LoginFriendViewModel.Event.StartOdya -> {
//                val action = LoginFriendFragmentDirections.actionLoginFriendFragmentToFragmentMap()
//                findNavController().navigate(action)
            }

            is LoginFriendViewModel.Event.CreateFollowSuccess -> {
                sendSnackBar("${event.nickname}님과 팔로우가 되었습니다")
            }
        }
    }
}
