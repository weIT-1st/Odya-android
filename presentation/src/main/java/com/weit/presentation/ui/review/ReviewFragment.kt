package com.weit.presentation.ui.review

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
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ReviewFragment : DialogFragment() {

    private val viewModel: ReviewViewModel by viewModels()
    private var _binding: FragmentDialogCreateReviewBinding? = null
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
        _binding = FragmentDialogCreateReviewBinding.inflate(inflater, container, false)
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
                binding.tvCreateReviewDetial.text = String.format(
                    getString(R.string.create_review_detail),
                    review.length,
                )
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.rating.collectLatest { rating ->
                viewModel.setStar(rating)
                binding.tvCreateReviewStar.text = String.format(
                    getString(R.string.create_review_star),
                    rating,
                )
            }
        }

        binding.btnCreateReviewRegister.setOnClickListener {
            viewModel.registerPlaceReview()
        }

        binding.btnCreateReviewCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleEvent(event: ReviewViewModel.Event) {
        when (event) {
            ReviewViewModel.Event.RegistrationSuccess -> {
                sendSnackBar("한줄 리뷰 성공")
            }
            ReviewViewModel.Event.TooLongReviewError -> {
                sendSnackBar("리뷰가 너무 길어요")
            }
            ReviewViewModel.Event.TooShortReviewError -> {
                sendSnackBar("리뷰를 입력하세요")
            }
            ReviewViewModel.Event.IsDuplicatedReviewError -> {
                sendSnackBar("중복된 리뷰 이빈다.")
            }
            ReviewViewModel.Event.NotEnoughStarError -> {
                sendSnackBar("별점을 입력하세요")
            }
            ReviewViewModel.Event.TooManyStarError -> {
                sendSnackBar("별점이 너무 커요")
            }
            ReviewViewModel.Event.InvalidTokenError -> {
                sendSnackBar("권한이 없어요")
            }
            ReviewViewModel.Event.UnregisteredError -> {
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
