package com.weit.presentation.ui.searchplace.editreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentBottomSheetReviewBinding
import com.weit.presentation.ui.util.repeatOnStarted
import com.weit.presentation.util.PlaceReviewContentData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class EditPlaceReviewFragment(
    private val updateReviewList: () -> Unit,
    private val placeId: String,
    private val placeReviewContentData: PlaceReviewContentData?,
) : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: EditPlaceReviewViewModel.PlaceReviewContentFactory

    private val viewModel: EditPlaceReviewViewModel by viewModels {
        EditPlaceReviewViewModel.provideFactory(viewModelFactory, placeReviewContentData)
    }

    private var _binding: FragmentBottomSheetReviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBottomSheetReviewBinding.inflate(inflater, container, false)
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (placeReviewContentData != null) {
            binding.tvEditReviewTitle.setText(R.string.edit_review_title)
            binding.btnEditReviewRegister.setText(R.string.edit_review_register)
            binding.etEditReviewDetail.hint = placeReviewContentData.myReview
        }

        binding.ratingbarEditReviewStar.setOnRatingChangeListener { _, rating, _ ->
            viewModel.setRating(rating)
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }

        binding.btnEditReviewRegister.setOnClickListener {
            viewModel.updatePlaceReview(placeId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleEvent(event: EditPlaceReviewViewModel.Event) {
        when (event) {
            EditPlaceReviewViewModel.Event.RegistrationSuccess -> {
                updateReviewList()
                sendSnackBar("한줄 리뷰 성공")
                dismiss()
            }
            EditPlaceReviewViewModel.Event.TooLongReviewError -> {
                sendSnackBar("리뷰가 너무 길어요")
            }
            EditPlaceReviewViewModel.Event.TooShortReviewError -> {
                sendSnackBar("리뷰를 입력하세요")
            }
            EditPlaceReviewViewModel.Event.InvalidRequestError -> {
                sendSnackBar("다시 입력하세요")
            }
            EditPlaceReviewViewModel.Event.NotEnoughStarError -> {
                sendSnackBar("별점을 입력하세요")
            }
            EditPlaceReviewViewModel.Event.TooManyStarError -> {
                sendSnackBar("별점이 너무 커요")
            }
            EditPlaceReviewViewModel.Event.InvalidTokenError -> {
                sendSnackBar("권한이 없어요")
            }
            EditPlaceReviewViewModel.Event.UnregisteredError -> {
                sendSnackBar("로그인 하세요")
            }
            EditPlaceReviewViewModel.Event.AlreadyRegisterReviewError -> {
                sendSnackBar("이미 리뷰가 존재합니다.")
            }
            EditPlaceReviewViewModel.Event.NotExistPlaceReview -> {
                sendSnackBar("수정할 리뷰가 없어요")
            }
        }
    }

    private fun sendSnackBar(
        message: String,
        @IntRange(from = -2) length: Int = Snackbar.LENGTH_SHORT,
        anchorView: View? = null,
    ) {
        Snackbar.make(
            binding.root,
            message,
            length,
        ).apply {
            if (anchorView != null) {
                this.anchorView = anchorView
            }
        }.show()
    }
}
