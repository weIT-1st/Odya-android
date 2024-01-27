package com.weit.presentation.ui.profile.preferences.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.databinding.FragmentSettingBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(
    FragmentSettingBinding::inflate
) {
    val viewModel : SettingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initListener() {
        binding.tbSetting.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.isAlarmAll.collectLatest {
                viewModel.setIsAlarmAll(it, true)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.isAlarmLike.collectLatest {
                viewModel.setIsAlarmLike(it, false)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.isAlarmComment.collectLatest {
                viewModel.setIsAlarmComment(it, false)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.isAlarmMarketing.collectLatest {
                viewModel.setIsAlarmMarketing(it, false)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.isLocationInfo.collectLatest {
                viewModel.setIsLocationInfo(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: SettingViewModel.Event) {
        when (event) {
            is SettingViewModel.Event.MoveToSettingDetail -> {
                val action = SettingFragmentDirections.actionSettingFragmentToSettingDetailFragment(event.detailType)
                findNavController().navigate(action)
            }

            SettingViewModel.Event.MoveToUpdateInfo -> {
                val action = SettingFragmentDirections.actionSettingFragmentToUpdateUserInfoFragment()
                findNavController().navigate(action)
            }
        }
    }
}