package com.weit.presentation.ui.feed.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.presentation.R
import com.weit.presentation.databinding.BottomSheetFeedCommentBinding
import com.weit.presentation.model.FeedComment
import com.weit.presentation.ui.util.SpaceDecoration

class CommentDialogFragment(feedId: Long?) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetFeedCommentBinding? = null
    private val binding get() = _binding!!
    private val feedCommentAdapter = FeedCommentAdapter()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetFeedCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFeedComments.run {
            addItemDecoration(
                SpaceDecoration(
                    resources,
                    topDP = R.dimen.item_feed_comment_space,
                    bottomDP = R.dimen.item_feed_comment_space,
                ),
            )
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
            adapter = feedCommentAdapter
        }

        //TODO 댓글 API 호출
//        feedCommentAdapter.submitList()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvFeedComments.adapter = null
        _binding = null
    }
    companion object {
        const val TAG = "CommentDialog"
    }
}
