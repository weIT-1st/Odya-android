package com.weit.presentation.ui.searchplace.review

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentTabPlaceReviewBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.searchplace.editreview.EditPlaceReviewFragment
import com.weit.presentation.ui.searchplace.report.ReviewReportFragment
import com.weit.presentation.ui.util.repeatOnStarted
import com.weit.presentation.util.PlaceReviewContentData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class PlaceReviewFragment(
    private val placeId: String,
    private val placeTitle: String,
) : BaseFragment<FragmentTabPlaceReviewBinding>(
    FragmentTabPlaceReviewBinding::inflate,
) {
    @Inject
    lateinit var viewModelFactory: PlaceReviewViewModel.PlaceIdFactory

    private val viewModel: PlaceReviewViewModel by viewModels {
        PlaceReviewViewModel.provideFactory(viewModelFactory, placeId)
    }

    private var editPlaceReviewFragment: EditPlaceReviewFragment? = null
    private val placeReviewAdapter: PlaceReviewAdapter by lazy {
        PlaceReviewAdapter(
            { viewModel.onClickCreateReview() },
            { viewModel.deleteMyReview() },
            { showReviewReport(it)}
        )
    }

    private var reviewReportFragment: ReviewReportFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPlaceReviewRV()
        binding.tvTabHowAboutThis.text = getString(R.string.place_how_about, placeTitle)
    }

    override fun initListener() {
        binding.btnTabCreateReview.setOnClickListener {
            viewModel.onClickCreateReview()
        }
    }
    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handelEvent(event)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.placeReviewList.collectLatest { list ->
                placeReviewAdapter.submitList(list)

                val mainText = getString(R.string.place_review_count, list.size)
                val spannableStringBuilder = SpannableStringBuilder(mainText)
                spannableStringBuilder.apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary)),
                        5,
                        5 + list.size.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                }
                binding.tvTabPlaceRecent.text = spannableStringBuilder
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.reviewRating.collectLatest { rating ->
                binding.tvTabPlaceReviewScore.text = String.format(getString(R.string.place_score), rating)
                binding.ratingbarTabPlaceReview.rating = rating
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.myPlaceReviewData.collectLatest { data ->
                if (data != null) {
                    binding.lyTabCreateReview.visibility = View.GONE
                } else {
                    binding.lyTabCreateReview.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvTabPlaceReview.adapter = null
        super.onDestroyView()
    }

    private fun initPlaceReviewRV() {
        binding.rvTabPlaceReview.adapter = placeReviewAdapter
    }

    private fun updateMyReview(myReviewData: PlaceReviewContentData?) {
        if (editPlaceReviewFragment == null) {
            editPlaceReviewFragment = EditPlaceReviewFragment({ updateReviewList() }, placeId, myReviewData)
        }
        if (!editPlaceReviewFragment!!.isAdded) {
            editPlaceReviewFragment!!.show(childFragmentManager, "Edit Dialog")
        }
    }

    private fun updateReviewList() {
        viewModel.getReviewInfo()
    }

    private fun showReviewReport(placeReviewId: Long){
        if (reviewReportFragment == null){
            reviewReportFragment = ReviewReportFragment(placeReviewId)
        }

        if (!reviewReportFragment!!.isAdded){
            reviewReportFragment!!.show(childFragmentManager, "Report Dialog")
        }
    }

    private fun handelEvent(event: PlaceReviewViewModel.Event) {
        when (event) {
            is PlaceReviewViewModel.Event.GetAverageRatingSuccess -> {
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
            is PlaceReviewViewModel.Event.PopUpEditReview -> {
                updateMyReview(event.myReview)
            }
        }
    }
}
