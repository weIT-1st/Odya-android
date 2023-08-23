package com.weit.presentation.ui.placereview.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentDialogCreateReviewBinding
import com.weit.presentation.databinding.FragmentDialogEditReviewBinding
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EditPlaceReviewFragment : DialogFragment() {

    private val viewModel: EditPlaceReviewViewModel by viewModels()
    private var _binding: FragmentDialogEditReviewBinding? = null
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
        _binding = FragmentDialogEditReviewBinding.inflate(inflater, container, false)
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.review.collectLatest { review ->
                binding.tvEditReviewDetail.text = String.format(
                    getString(R.string.review_detail),
                    review.length
                )
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.rating.collectLatest { rating ->
                viewModel.setStar(rating)
                binding.tvEditReviewStar.text = String.format(
                   getString(R.string.review_star),
                    rating
               )
            }
        }

        binding.btnEditReviewRegister.setOnClickListener {
            viewModel.updatePlaceReview()
        }

        binding.btnEditReviewCancel.setOnClickListener {
            dismiss()
        }

        binding.etEditReviewDetail.hint = viewModel.existReview

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
            else -> {}
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
