package com.weit.presentation.ui.travellog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.orhanobut.logger.Logger
import com.weit.presentation.databinding.FragmentTravellogBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TravelLogFragment : BaseFragment<FragmentTravellogBinding>(
    FragmentTravellogBinding::inflate,
) {
    private val args: TravelLogFragmentArgs by navArgs()
    private val viewModel: TravelLogViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        Logger.t("MainTest").i("TravelLogFragment에서 받은 args${args.travelLogId}")
    }

    override fun initCollector() {
    }
}
