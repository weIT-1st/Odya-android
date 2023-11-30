package com.weit.presentation.ui.feed.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFeedSearchBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FeedSearchFragment : BaseFragment<FragmentFeedSearchBinding>(
    FragmentFeedSearchBinding::inflate,
) {

    private val viewModel: FeedSearchViewModel by viewModels()
    private val recentUserSearchAdapter = RecentUserSearchAdapter(
        deleteItem = { user -> viewModel.deleteRecentUserSearch(user)},
    )
    private val searchUserResultAdapter = SearchUserResultAdapter(
        selectUser = { user ->
            navigateToProfile()
            viewModel.plusRecentUserSearch(user)},
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initRecyclerView()
    }

    private fun navigateToProfile(){
        val action = FeedSearchFragmentDirections.actionFragmentFeedSearchToFragmentMypage()
        findNavController().navigate(action)
    }

    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextUsers()
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvFeedSearchResult.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            addOnScrollListener(infinityScrollListener)
            adapter = searchUserResultAdapter
        }
        binding.rvFeedRecentSearch.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            adapter = recentUserSearchAdapter
        }

    }

    override fun initListener() {
        binding.btnFeedSearchCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.query.collectLatest { query ->
                val isQueryEmpty = query.isEmpty()
                binding.rvFeedRecentSearch.isVisible = isQueryEmpty
                binding.tvFeedSearchTitle.isVisible = isQueryEmpty
                binding.rvFeedSearchResult.isVisible = !isQueryEmpty
                if (!isQueryEmpty) {
                    viewModel.onSearchUser(query)
                }
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.recentSearchUsers.collectLatest { users ->
                recentUserSearchAdapter.submitList(users)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.searchResultUsers.collectLatest { users ->
                searchUserResultAdapter.submitList(users)
            }
        }
    }

    override fun onDestroyView() {
        binding.rvFeedRecentSearch.removeOnScrollListener(infinityScrollListener)
        binding.rvFeedRecentSearch.adapter = null
        binding.rvFeedSearchResult.adapter = null
        super.onDestroyView()
    }

}
