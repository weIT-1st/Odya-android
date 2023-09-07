package com.weit.presentation.ui.searchplace.review

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentTabPlaceReviewBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.placereview.EditPlaceReviewFragment
import com.weit.presentation.ui.util.repeatOnStarted
import com.weit.presentation.util.PlaceReviewContentData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class PlaceReviewFragment(
    private val placeId: String,
) : BaseFragment<FragmentTabPlaceReviewBinding>(
    FragmentTabPlaceReviewBinding::inflate
) {
    @Inject
    lateinit var viewModelFactory: PlaceReviewViewModel.PlaceIdFactory

    private val viewModel: PlaceReviewViewModel by viewModels{
        PlaceReviewViewModel.provideFactory(viewModelFactory, placeId)
    }

    private var editPlaceReviewFragment: EditPlaceReviewFragment? = null
    private val placeReviewAdapter:PlaceReviewAdapter by lazy {
        PlaceReviewAdapter(
        context, { updateMyReview() }, { viewModel.deleteMyReview() }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initPlaceReviewRV()
    }
    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.placeReviewList.collectLatest {
                placeReviewAdapter.submitList(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.reviewRating.collectLatest { rating ->
                binding.tvTabPlaceRecent20.text = String.format(getString(R.string.place_recent_review), viewModel.reviewNum)
                binding.tvTabPlaceReviewScore.text = String.format(getString(R.string.place_score), rating)
                binding.ratingbarTabPlaceReview.rating = rating
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        editPlaceReviewFragment = null
        binding.rvTabPlaceReview.adapter = null
    }

    private fun initPlaceReviewRV(){
        binding.rvTabPlaceReview.adapter = placeReviewAdapter
    }

    private fun updateMyReview(){
        Log.d("UpdateReview", "operation~")
        if(editPlaceReviewFragment == null){
            editPlaceReviewFragment = EditPlaceReviewFragment(placeId, PlaceReviewContentData(viewModel.myPlaceReviewID.value, viewModel.myReview.value, (viewModel.myRating.value*2).toInt()))
        }

        if (!editPlaceReviewFragment!!.isAdded){
            editPlaceReviewFragment!!.show(childFragmentManager, "Edit Dialog")
        }
    }
}
