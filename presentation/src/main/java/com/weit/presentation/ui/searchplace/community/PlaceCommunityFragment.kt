package com.weit.presentation.ui.searchplace.community

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentTabPlaceCommunityBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaceCommunityFragment() : BaseFragment<FragmentTabPlaceCommunityBinding>(
    FragmentTabPlaceCommunityBinding::inflate,
) {
    private val viewModel: PlaceCommunityViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }
}
