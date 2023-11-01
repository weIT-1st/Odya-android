package com.weit.presentation.ui.feed.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orhanobut.logger.Logger
import com.weit.presentation.R
import com.weit.presentation.databinding.BottomSheetFeedCommentBinding
import com.weit.presentation.model.FeedComment
import com.weit.presentation.model.FeedDetail
import com.weit.presentation.ui.placereview.EditPlaceReviewViewModel
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class CommentDialogFragment(val feed: FeedDetail) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetFeedCommentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: CommentDialogViewModel.FeedDetailFactory

    private val viewModel: CommentDialogViewModel by viewModels {
        CommentDialogViewModel.provideFactory(viewModelFactory,feed)
    }

    private val feedCommentAdapter = FeedCommentAdapter(
        updateItem = { position -> viewModel.updateComment(position) },
        deleteItem = { position -> viewModel.deleteComment(position)},
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetFeedCommentBinding.inflate(inflater, container, false)

        binding.vm = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initCollector()
    }

    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextComments()
            }
        }
    }
    fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.comments.collectLatest { comments ->
                feedCommentAdapter.submitList(comments)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.changedComment.collectLatest { content ->
                binding.etFeedComment.setText(content)
            }
        }
    }

    private fun initRecyclerView(){
        binding.rvFeedComments.run {
            addItemDecoration(
                SpaceDecoration(
                    resources,
                    topDP = R.dimen.item_feed_comment_space,
                    bottomDP = R.dimen.item_feed_comment_space,
                ),
            )
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
            addOnScrollListener(infinityScrollListener)
            adapter = feedCommentAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvFeedComments.removeOnScrollListener(infinityScrollListener)
        binding.rvFeedComments.adapter = null
        _binding = null
    }
    companion object {
        const val TAG = "CommentDialog"
    }
}
