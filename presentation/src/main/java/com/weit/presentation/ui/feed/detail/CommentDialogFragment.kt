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

class CommentDialogFragment() : BottomSheetDialogFragment() {
    private var _binding: BottomSheetFeedCommentBinding? = null
    private val binding get() = _binding!!
    private val feedCommentAdapter = FeedCommentAdapter()
    var comments = listOf<FeedComment>()

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
                SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space),
            )
            addItemDecoration(
                SpaceDecoration(resources, topDP = R.dimen.item_travel_friend_search_space),
            )
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
            adapter = feedCommentAdapter
        }
        feedCommentAdapter.submitList(comments)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val TAG = "CommentDialog"
    }
}
