package com.weit.presentation.ui.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.orhanobut.logger.Logger
import com.weit.presentation.databinding.FragmentFeedDetailBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.detail.FeedDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedDetailFragment : BaseFragment<FragmentFeedDetailBinding>(
    FragmentFeedDetailBinding::inflate,
) {

    private val viewModel: FeedDetailViewModel by viewModels()
    private val args: FeedDetailFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        Logger.t("MainTest").i("feedDetail에서 args${args.feedId}")
    }

    override fun initCollector() {
    }
}
