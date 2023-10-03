package com.weit.presentation.ui.searchplace.journey

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentTabPlaceJourneyBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaceJourneyFragment : BaseFragment<FragmentTabPlaceJourneyBinding> (
    FragmentTabPlaceJourneyBinding::inflate,
) {
    private val viewModel: PlaceJourneyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
