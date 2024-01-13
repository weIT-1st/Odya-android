package com.weit.presentation.ui.searchplace.journey

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.marginEnd
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentTabPlaceJourneyBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.DimensionUtils
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class PlaceJourneyFragment(
    private val placeId: String
) : BaseFragment<FragmentTabPlaceJourneyBinding> (
    FragmentTabPlaceJourneyBinding::inflate,
) {
    @Inject
    lateinit var viewModelFactory: PlaceJourneyViewModel.PlaceIdFactory

    private val viewModel: PlaceJourneyViewModel by viewModels{
        PlaceJourneyViewModel.provideFactory(viewModelFactory, placeId)
    }

    private val myJournalAdapter: MyJournalAdapter = MyJournalAdapter(placeId)
    private val friendJournalAdapter: FriendJournalAdapter = FriendJournalAdapter()
    private val recommendJournalAdapter: RecommendJournalAdapter = RecommendJournalAdapter() 

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMyJournalRecyclerView()
        setFriendJournalRecyclerView()
        setRecommendJournalRecyclerView()
    }


    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.myJournalDetail.collectLatest { detail ->
                myJournalAdapter.submitList(detail)

                binding.includeTabPlaceNoMyJournal.root.isGone = detail.isNotEmpty()
                binding.tvTabPlaceMyJournal.isGone = detail.isEmpty()
                binding.rvTabPlaceMyJournal.isGone = detail.isEmpty()
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.friendJournalList.collectLatest { friendJournals ->
                friendJournalAdapter.submitList(friendJournals)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.recommendJournalList.collectLatest { recommendJournals ->
                recommendJournalAdapter.submitList(recommendJournals)
            }
        }
    }

    override fun onDestroyView() {
        binding.rvTabPlaceMyJournal.adapter = null
        binding.rvTabPlaceFriendJourney.adapter = null
        binding.rvTabPlaceRecommendJourney.adapter = null
        super.onDestroyView()
    }

    private fun setMyJournalRecyclerView(){
        binding.rvTabPlaceMyJournal.apply {
            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.main_my_journal_margin))
            adapter = myJournalAdapter
        }
    }

    private fun setFriendJournalRecyclerView(){
        binding.rvTabPlaceFriendJourney.adapter = friendJournalAdapter
    }

    private fun setRecommendJournalRecyclerView(){
        binding.rvTabPlaceRecommendJourney.adapter = recommendJournalAdapter
    }
}
