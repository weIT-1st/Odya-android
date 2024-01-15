package com.weit.presentation.ui.main.journal

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentTabPlaceJourneyBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class PlaceJournalFragment(
    private val placeId: String
) : BaseFragment<FragmentTabPlaceJourneyBinding> (
    FragmentTabPlaceJourneyBinding::inflate,
) {
    @Inject
    lateinit var viewModelFactory: PlaceJournalViewModel.PlaceIdFactory

    private val viewModel: PlaceJournalViewModel by viewModels{
        PlaceJournalViewModel.provideFactory(viewModelFactory, placeId)
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

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
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
