package com.weit.presentation.ui.friendmanage.my

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.weit.domain.model.follow.FollowUserContent
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentMyFriendManageBinding
import com.weit.presentation.model.Follow
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.friendmanage.showDeleteFollowerDialog
import com.weit.presentation.ui.friendmanage.showDeleteFollowingDialog
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MyFriendManageFragment : BaseFragment<FragmentMyFriendManageBinding>(
    FragmentMyFriendManageBinding::inflate,
) {
    private val viewModel: MyFriendManageViewModel by viewModels()

    private val followingSearchAdapter = FollowingSearchAdapter { user ->
        context?.let { showDeleteFollowingDialog(it) { viewModel.selectSearchFollowing(user) } }
    }

    private val myFollowingAdapter = MyFollowingAdapter { user ->
        context?.let { showDeleteFollowingDialog(it) { viewModel.selectDefaultFollowing(user) } }
    }

    private val followerSearchAdapter = FollowerSearchAdapter{ user ->
        context?.let { showDeleteFollowerDialog(it) { viewModel.selectSearchFollower(user) } }
    }

    private val myFollowerAdapter = MyFollowerAdapter{ user ->
        context?.let { showDeleteFollowerDialog(it) { viewModel.selectDefaultFollower(user) } }
    }

    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            viewModel.selectFollowMenu(Follow.fromPosition(tab.position))
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }


    private val infinitySearchScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onSearchMoreUser()
            }
        }
    }

    private val infinityDefaultScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onDefault()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.tlFriendManageFollow.addOnTabSelectedListener(tabSelectedListener)
        initRv()
    }

    private fun initRv(){
        binding.rvFriendManageSearchFollower.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            addOnScrollListener(infinitySearchScrollListener)
            adapter = followerSearchAdapter
        }
        binding.rvFriendManageSearchFollowing.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            addOnScrollListener(infinitySearchScrollListener)
            adapter = followingSearchAdapter
        }
        binding.rvFriendManageDefaultFollower.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            addOnScrollListener(infinityDefaultScrollListener)
            adapter = myFollowerAdapter
        }
        binding.rvFriendManageDefaultFollowing.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            addOnScrollListener(infinityDefaultScrollListener)
            adapter = myFollowingAdapter
        }
    }

    override fun initListener() {
        binding.tbFriendManage.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collect { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.query.collectLatest { query ->
                val isQueryEmpty = query.isEmpty()
                if (!isQueryEmpty) {
                    viewModel.updateSearchRv()
                    viewModel.onSearch(query)
                }else{
                    viewModel.initData()
                }
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.searchResultFollowings.collectLatest { users ->
                followingSearchAdapter.submitList(users)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.searchResultFollowers.collectLatest { users ->
                followerSearchAdapter.submitList(users)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.defaultFollowers.collectLatest { users ->
                myFollowerAdapter.submitList(users)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.defaultFollowings.collectLatest { users ->
                myFollowingAdapter.submitList(users)
            }
        }
    }

    private fun handleEvent(event: MyFriendManageViewModel.Event) {
        when (event) {
            is MyFriendManageViewModel.Event.OnUpdateSearchRv -> {
                if(event.followState == Follow.FOLLOWING) {
                    binding.rvFriendManageSearchFollowing.isVisible = true
                    binding.rvFriendManageSearchFollower.isVisible = false
                    binding.rvFriendManageDefaultFollowing.isVisible = false
                    binding.rvFriendManageDefaultFollower.isVisible = false
                }else{
                    binding.rvFriendManageSearchFollowing.isVisible = false
                    binding.rvFriendManageSearchFollower.isVisible = true
                    binding.rvFriendManageDefaultFollowing.isVisible = false
                    binding.rvFriendManageDefaultFollower.isVisible = false
                }
            }
            is MyFriendManageViewModel.Event.OnUpdateDefaultRv -> {
                if(event.followState == Follow.FOLLOWING) {
                    binding.rvFriendManageSearchFollowing.isVisible = false
                    binding.rvFriendManageSearchFollower.isVisible = false
                    binding.rvFriendManageDefaultFollowing.isVisible = true
                    binding.rvFriendManageDefaultFollower.isVisible = false
                }else{
                    binding.rvFriendManageSearchFollowing.isVisible = false
                    binding.rvFriendManageSearchFollower.isVisible = false
                    binding.rvFriendManageDefaultFollowing.isVisible = false
                    binding.rvFriendManageDefaultFollower.isVisible = true
                }
            }
            else -> {}
        }
    }

    override fun onDestroyView() {
        binding.rvFriendManageSearchFollowing.removeOnScrollListener(infinitySearchScrollListener)
        binding.rvFriendManageSearchFollower.removeOnScrollListener(infinitySearchScrollListener)
        binding.rvFriendManageDefaultFollower.removeOnScrollListener(infinityDefaultScrollListener)
        binding.rvFriendManageDefaultFollowing.removeOnScrollListener(infinityDefaultScrollListener)
        binding.rvFriendManageDefaultFollowing.adapter = null
        binding.rvFriendManageDefaultFollower.adapter = null
        binding.rvFriendManageSearchFollower.adapter = null
        binding.rvFriendManageSearchFollowing.adapter = null
        super.onDestroyView()
    }
}
