package com.weit.presentation.ui.feed.myactivity.comment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentTabFeedCommentBinding
import com.weit.presentation.databinding.FragmentTabPlaceCommunityBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedMyActivityCommentFragment() : BaseFragment<FragmentTabFeedCommentBinding>(
    FragmentTabFeedCommentBinding::inflate,
) {
    private val viewModel: FeedMyActivityCommentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
