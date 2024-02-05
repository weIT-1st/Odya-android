package com.weit.presentation.ui.main.journal

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentTabPlaceJourneyBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.journal.friends.TravelJournalFriendAdapter
import com.weit.presentation.ui.journal.map.PinMode
import com.weit.presentation.ui.journal.map.TravelJournalMapFragment
import com.weit.presentation.ui.profile.reptraveljournal.TogetherFriendBottomFragment
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

    private var togetherFriendBottomFragment: TogetherFriendBottomFragment? = null
    private val travelFriendJournalAdapter = TravelJournalFriendAdapter()
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
                binding.includeTabPlaceNoMyJournal.root.isGone = (journal != null)

                binding.lyTabPlaceMyJournal.isGone = (journal == null)
                binding.lyTabPlaceMyJournalContent.isGone = (journal == null)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.myRandomJournalInfo.collectLatest { info ->
                info?.let {
                    setMyTravelJournal(info)
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
        binding.includeTabPlaceMyJournal.rvItemMyJournalFriends.adapter = null
        binding.rvTabPlaceFriendJournal.adapter = null
        binding.rvTabPlaceRecommendJournal.adapter = null

        binding.rvTabPlaceFriendJournal.removeOnScrollListener(friendInfinityScrollListener)
        binding.rvTabPlaceRecommendJournal.removeOnScrollListener(recommendInfinityScrollListener)
        super.onDestroyView()
    }

    private fun setMyTravelJournal(info: TravelJournalInfo) {
        childFragmentManager.beginTransaction()
            .add(R.id.fragment_item_my_journal_map,
                TravelJournalMapFragment(
                    travelJournalInfo = info,
                    pinMode = PinMode.IMAGE_PIN,
                    isMapLine = true
                ))
            .setReorderingAllowed(true)
            .commit()

        binding.lyTabPlaceMyJournalContent.setOnClickListener {
            moveToTravelJournalDetail(info.travelJournalId)
        }
        binding.includeTabPlaceMyJournal.tvItemMyJournalTitle.text = info.travelJournalTitle
        binding.includeTabPlaceMyJournal.tvItemMyJournalDate.text =
            requireContext().getString(R.string.place_journey_date, info.travelStartDate, info.travelEndDate)
        binding.tvTabPlaceMyJournalContent.text = info.travelJournalContents.first().content
        binding.includeTabPlaceMyJournal.btnItemMyJournalMoreFriend.isGone =
            info.travelJournalCompanions.size < DEFAULT_FRIEND_COUNT
        binding.includeTabPlaceMyJournal.rvItemMyJournalFriends.apply {
            adapter = travelFriendJournalAdapter
            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.item_journal_friends_space))
        }
        travelFriendJournalAdapter.submitList(viewModel.handleFriendsCount(info))
        binding.includeTabPlaceMyJournal.btnItemMyJournalMoreFriend.setOnClickListener {
            if (togetherFriendBottomFragment == null) {
                togetherFriendBottomFragment = TogetherFriendBottomFragment(info.travelJournalCompanions)

            }
            if (togetherFriendBottomFragment?.isAdded?.not() == true) {
                togetherFriendBottomFragment?.show(
                    requireActivity().supportFragmentManager,
                    TogetherFriendBottomFragment.TAG,
                )
            }
        }
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
        val action = PlaceJournalFragmentDirections.actionPlaceJournalFragmentToFragmentTravelJournal(
            travelJournalId = travelJournalId
        )
        findNavController().navigate(action)
    }

    private fun moveToPostTravelJournal() {
        val action = PlaceJournalFragmentDirections.actionPlaceJournalFragmentToPostGraph()
        findNavController().navigate(action)
    }

    companion object {
        const val DEFAULT_FRIEND_COUNT = 3
    }
}
