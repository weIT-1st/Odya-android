package com.weit.presentation.ui.feed.myactivity.like

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentTabFeedLikeBinding
import com.weit.presentation.databinding.FragmentTabFeedPostBinding
import com.weit.presentation.databinding.FragmentTabPlaceCommunityBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedMyActivityLikeFragment() : BaseFragment<FragmentTabFeedLikeBinding>(
    FragmentTabFeedLikeBinding::inflate,
) {
    private val viewModel: FeedMyActivityLikeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
