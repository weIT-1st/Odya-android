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
import com.weit.presentation.ui.searchplace.SearchPlaceBottomSheetViewModel
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
        { updateMyReview() }, { viewModel.deleteMyReview() }
        )
    }

    private var myPlaceReviewData: PlaceReviewContentData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initPlaceReviewRV()
    }
    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.event.collectLatest { event ->
                handelEvent(event)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.placeReviewList.collectLatest {
                placeReviewAdapter.submitList(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.reviewRating.collectLatest { rating ->
                binding.tvTabPlaceRecent.text = String.format(getString(R.string.place_recent_review), viewModel.reviewNum)
                binding.tvTabPlaceReviewScore.text = String.format(getString(R.string.place_score), rating)
                binding.ratingbarTabPlaceReview.rating = rating
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.myPlaceReviewData.collectLatest { data ->
                myPlaceReviewData = data
            }
        }
    }

    override fun onDestroyView() {
        binding.rvTabPlaceReview.adapter = null
        super.onDestroyView()
    }

    private fun initPlaceReviewRV(){
        binding.rvTabPlaceReview.adapter = placeReviewAdapter
    }


    private fun updateMyReview(){
        if(editPlaceReviewFragment == null){
            if (myPlaceReviewData != null){
                editPlaceReviewFragment = EditPlaceReviewFragment({ updateReviewList() }, placeId, myPlaceReviewData)
            }
        }
        if (!editPlaceReviewFragment!!.isAdded){
            editPlaceReviewFragment!!.show(childFragmentManager, "Edit Dialog")
        }
    }

    private fun updateReviewList() {
        viewModel.getPlaceReview()
    }

    private fun handelEvent(event: PlaceReviewViewModel.Event){
        when(event){
            is PlaceReviewViewModel.Event.GetAverageRatingSuccess -> {
                // 어떤 조취를 취할까요?
            }
            is PlaceReviewViewModel.Event.GetPlaceReviewWithMineSuccess -> {
                sendSnackBar("나의 리뷰를 함께 가져왔습니다.")
            }
            is PlaceReviewViewModel.Event.GetPlaceReviewSuccess -> {
                sendSnackBar("리뷰를 가져왔습니다.")
            }
            is PlaceReviewViewModel.Event.InvalidRequestException -> {
                sendSnackBar("잘못된 placeId를 가져오고 있어요")
            }
            is PlaceReviewViewModel.Event.InvalidTokenException -> {
                sendSnackBar("유효하지 않은 토큰 입니다.")
            }
            is PlaceReviewViewModel.Event.UnKnownException -> {
                sendSnackBar("알 수 없는 에러 발생")
            }
            PlaceReviewViewModel.Event.ForbiddenException -> {
                sendSnackBar("작성자만 삭제할 수 있습니다.")
            }
            PlaceReviewViewModel.Event.NotFoundException -> {
                sendSnackBar("존재하지 않는 장소 리뷰입니다.")
            }
            PlaceReviewViewModel.Event.DeleteMyReviewSuccess -> {
                sendSnackBar("성공적으로 리뷰가 삭제되었습니다..")
            }
            PlaceReviewViewModel.Event.UpdateMyReviewSuccess -> {
                sendSnackBar("성공적으로 리뷰가 수정 되었습니다.")
            }
            PlaceReviewViewModel.Event.DoNotGetMyReviewData -> {
                sendSnackBar("내가 작성한 리뷰 데이터를 가져오지 못했습니다.")
            }
        }
    }
}
