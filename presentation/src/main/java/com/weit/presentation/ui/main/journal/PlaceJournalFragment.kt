package com.weit.presentation.ui.main.journal

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentTabPlaceJourneyBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.InfinityScrollListener
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

    private val friendJournalAdapter: FriendJournalAdapter = FriendJournalAdapter()
    private val recommendJournalAdapter: RecommendJournalAdapter = RecommendJournalAdapter()

    private val friendInfinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextFriendJournal()
            }
        }
    }

    private val recommendInfinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextRecommendJournal()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFriendJournalRecyclerView()
        setRecommendJournalRecyclerView()
    }


    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.myRandomJournal.collectLatest { journal ->
                Log.d("jomi", "myJournal List : $journal")
                binding.includeTabPlaceNoMyJournal.root.isGone = (journal != null)

                binding.lyTabPlaceMyJournal.isGone = (journal == null)
                binding.lyTabPlaceMyJournalContent.isGone = (journal == null)

                if (journal != null) {
                    binding.lyTabPlaceMyJournal.setOnClickListener {
                        moveToTravelJournalDetail(journal.travelJournalId)
                    }
                    binding.includeTabPlaceMyJournal.tvItemMyJournalTitle.text = journal?.travelJournalTitle
                    binding.includeTabPlaceMyJournal.tvItemMyJournalDate.text =
                        requireContext().getString(R.string.place_journey_date, journal?.travelStartDate, journal?.travelEndDate)
                    binding.tvTabPlaceMyJournalContent.text = journal?.content

                    binding.includeTabPlaceMyJournal.btnItemMyJournalMoreFriend.isGone =
                        journal.travelCompanionSimpleResponses.size < DEFAULT_FRIEND_COUNT

                    // todo 친구 더보기 연결
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.friendJournalList.collectLatest { friendJournals ->
                friendJournalAdapter.submitList(friendJournals)
                binding.tvTabPlaceNoFriendJournal.isGone = friendJournals.isNotEmpty()
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.recommendJournalList.collectLatest { recommendJournals ->
                recommendJournalAdapter.submitList(recommendJournals)
                binding.tvTabPlaceNoRecommendJournal.isGone = recommendJournals.isNotEmpty()
            }
        }
    }

    override fun initListener() {
        binding.includeTabPlaceNoMyJournal.btnPlaceNoTravelLogWrite.setOnClickListener {
            moveToPostTravelJournal()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun onDestroyView() {
        binding.rvTabPlaceFriendJournal.adapter = null
        binding.rvTabPlaceRecommendJournal.adapter = null

        binding.rvTabPlaceFriendJournal.removeOnScrollListener(friendInfinityScrollListener)
        binding.rvTabPlaceRecommendJournal.removeOnScrollListener(recommendInfinityScrollListener)
        super.onDestroyView()
    }

    private fun setFriendJournalRecyclerView(){
        binding.rvTabPlaceFriendJournal.apply {
            adapter = friendJournalAdapter
            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.main_journal_margin))
            addOnScrollListener(friendInfinityScrollListener)
        }
    }

    private fun setRecommendJournalRecyclerView(){
        binding.rvTabPlaceRecommendJournal.apply {
            adapter = recommendJournalAdapter
            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.main_journal_margin))
            addOnScrollListener(recommendInfinityScrollListener)
        }
    }

    private fun moveToTravelJournalDetail(travelJournalId: Long){
        // todo 여행일지 상세 보기로 이동
    }

    private fun moveToPostTravelJournal() {
        val action = PlaceJournalFragmentDirections.actionPlaceJournalFragmentToPostGraph()
        findNavController().navigate(action)
    }

    companion object {
        const val DEFAULT_FRIEND_COUNT = 3
    }
}
