package com.weit.presentation.ui.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentFeedPostBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.post.FeedPostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedPostFragment : BaseFragment<FragmentFeedPostBinding>(
    FragmentFeedPostBinding::inflate,
) {

    private val viewModel: FeedPostViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm =viewModel
    }

    override fun initCollector() {
    }
}
