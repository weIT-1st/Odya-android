package com.weit.presentation.ui.memory

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFeedBinding
import com.weit.presentation.databinding.FragmentMemoryBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.FeedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemoryFragment : BaseFragment<FragmentMemoryBinding>(
    FragmentMemoryBinding::inflate,
) {

    private val viewModel: MemoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initCollector() {

    }

}