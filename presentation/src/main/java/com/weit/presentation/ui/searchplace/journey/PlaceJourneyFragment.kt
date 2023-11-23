package com.weit.presentation.ui.searchplace.journey

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentTabPlaceJourneyBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PlaceJourneyFragment(

) : BaseFragment<FragmentTabPlaceJourneyBinding> (
    FragmentTabPlaceJourneyBinding::inflate,
) {
    private val viewModel: PlaceJourneyViewModel by viewModels()

    private val myJournalAdapter: MyJournalAdapter by lazy { MyJournalAdapter() }
    private val friendJournalAdapter: FriendJournalAdapter by lazy { FriendJournalAdapter() }
    private val recommendJournalAdapter: RecommendJournalAdapter by lazy { RecommendJournalAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMyJournalRecyclerView()
        setFriendJournalRecyclerView()
        setRecommendJournalRecyclerView()
    }

    override fun initListener() {
        super.initListener()
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.myJournalList.collectLatest { myJournals ->
                myJournalAdapter.submitList(myJournals)
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
        binding.rvTabPlaceMyJourney.adapter = null
        binding.rvTabPlaceFriendJourney.adapter = null
        binding.rvTabPlaceRecommendJourney.adapter = null
        super.onDestroyView()
    }

    private fun setMyJournalRecyclerView(){
        binding.rvTabPlaceMyJourney.adapter = myJournalAdapter
    }

    private fun setFriendJournalRecyclerView(){
        binding.rvTabPlaceFriendJourney.adapter = friendJournalAdapter
    }

    private fun setRecommendJournalRecyclerView(){
        binding.rvTabPlaceRecommendJourney.adapter = recommendJournalAdapter
    }
}
