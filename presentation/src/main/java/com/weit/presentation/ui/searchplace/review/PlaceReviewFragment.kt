package com.weit.presentation.ui.searchplace.review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentTabPlaceReviewBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaceReviewFragment : BaseFragment<FragmentTabPlaceReviewBinding>(
    FragmentTabPlaceReviewBinding::inflate
) {
    private val viewModel: PlaceReviewViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }
}
