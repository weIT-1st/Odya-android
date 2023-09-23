package com.weit.presentation.ui.feed.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.presentation.R
import com.weit.presentation.databinding.BottomSheetFeedCommentBinding
import com.weit.presentation.model.FeedComment
import com.weit.presentation.model.FeedDetail
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import kotlinx.coroutines.flow.collectLatest

class CommentDialogFragment(val feed: FeedDetail?) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetFeedCommentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommentDialogViewModel by viewModels()

    private val feedCommentAdapter = FeedCommentAdapter(
        updateItem = { position -> changeComment(position) },
        deleteItem = { position -> viewModel.deleteComment(position)},
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetFeedCommentBinding.inflate(inflater, container, false)
        viewModel._feed.value = feed // 프로필변경ㅇ 오류가 없는지 확인
        viewModel.feedId = feed?.feedId ?: 0
        binding.vm = viewModel
        return binding.root
    }

    private fun changeComment(position:Int){
        binding.etFeedComment.setText(viewModel.commentList[position].content)
        viewModel.updateComment(position)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initCollector()
    }

    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.loadNextComments()
            }
        }
    }
    fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.comments.collectLatest { comments ->
                feedCommentAdapter.submitList(comments)
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
