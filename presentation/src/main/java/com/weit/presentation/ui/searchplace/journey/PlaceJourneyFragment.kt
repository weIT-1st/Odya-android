package com.weit.presentation.ui.searchplace.journey

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.databinding.FragmentTabPlaceJourneyBinding
import com.weit.presentation.ui.base.BaseFragment
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

    private val myJournalAdapter: MyJournalAdapter by lazy { MyJournalAdapter() }
    private val friendJournalAdapter: FriendJournalAdapter by lazy { FriendJournalAdapter() }
    private val recommendJournalAdapter: RecommendJournalAdapter by lazy { RecommendJournalAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMyJournalRecyclerView()
        setFriendJournalRecyclerView()
        setRecommendJournalRecyclerView()
    }


    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.myJournalList.collectLatest { myJournals ->
                myJournalAdapter.submitList(myJournals)
                binding.tvTabPlaceMyJourneyContent.text = myJournals.first().content
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
        binding.rvTabPlaceMyJourney.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)

                if (position != 0){
                    outRect.left = DimensionUtils.dpToPx(requireContext(), 10).toInt() * -1
                }
            }
        })
        binding.rvTabPlaceMyJourney.adapter = myJournalAdapter
    }

    private fun setFriendJournalRecyclerView(){
        binding.rvTabPlaceFriendJourney.adapter = friendJournalAdapter
    }

    private fun setRecommendJournalRecyclerView(){
        binding.rvTabPlaceRecommendJourney.adapter = recommendJournalAdapter
    }
}
