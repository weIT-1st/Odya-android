package com.weit.presentation.ui.placereview

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.IntRange
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentDialogEditReviewBinding
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
) : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: EditPlaceReviewViewModel.PlaceReviewContentFactory

    private val viewModel: EditPlaceReviewViewModel by viewModels {
        EditPlaceReviewViewModel.provideFactory(viewModelFactory, placeReviewContentData)
    }

    private var _binding: FragmentDialogEditReviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentDialogEditReviewBinding.inflate(inflater, container, false)
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

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.review.collectLatest { review ->
                binding.tvEditReviewDetail.text = String.format(
                    getString(R.string.review_detail),
                    review.length,
                )
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.rating.collectLatest { rating ->
                viewModel.setStar(rating)
                binding.tvEditReviewStar.text = String.format(
                    getString(R.string.review_star),
                    rating,
                )
            }
        }

        binding.btnEditReviewRegister.setOnClickListener {
            viewModel.updatePlaceReview(placeId)
            updateReviewList()
            dismiss()
        }

        binding.btnEditReviewCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleEvent(event: EditPlaceReviewViewModel.Event) {
        when (event) {
            EditPlaceReviewViewModel.Event.RegistrationSuccess -> {
                sendSnackBar("한줄 리뷰 성공")
            }
            EditPlaceReviewViewModel.Event.TooLongReviewError -> {
                sendSnackBar("리뷰가 너무 길어요")
            }
            EditPlaceReviewViewModel.Event.TooShortReviewError -> {
                sendSnackBar("리뷰를 입력하세요")
            }
            EditPlaceReviewViewModel.Event.IsDuplicatedReviewError -> {
                sendSnackBar("중복된 리뷰 이빈다.")
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
