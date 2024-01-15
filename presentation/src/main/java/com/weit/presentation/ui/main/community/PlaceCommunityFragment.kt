package com.weit.presentation.ui.main.community

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentTabPlaceCommunityBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class PlaceCommunityFragment(
    private val placeId: String
) : BaseFragment<FragmentTabPlaceCommunityBinding>(
    FragmentTabPlaceCommunityBinding::inflate,
) {
    @Inject
    lateinit var viewModelFactory: PlaceCommunityViewModel.PlaceIdFactory

    private val viewModel: PlaceCommunityViewModel by viewModels{
        PlaceCommunityViewModel.provideFactory(viewModelFactory, placeId)
    }

    private val communityAdapter = PlaceCommunityAdapter{
            communityId -> navigateFeedDetail(communityId)}

    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextImages()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvTabPlaceCommunity.run {
            addOnScrollListener(infinityScrollListener)
            adapter = communityAdapter
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.postImages.collectLatest { images ->
                communityAdapter.submitList(images)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun onDestroyView() {
        binding.rvTabPlaceCommunity.removeOnScrollListener(infinityScrollListener)
        binding.rvTabPlaceCommunity.adapter = null
        super.onDestroyView()
    }

    private fun navigateFeedDetail(feedId: Long) {
//        val action = FeedMyActivityPostFragmentDirections.actionFeedMyActivityPostFragmentToFragmentFeedDetail(feedId)
//        findNavController().navigate(action)
    }
}
