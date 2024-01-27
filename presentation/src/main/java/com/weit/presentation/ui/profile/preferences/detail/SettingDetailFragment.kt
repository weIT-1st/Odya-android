package com.weit.presentation.ui.profile.preferences.detail

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentSettingDetailBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SettingDetailFragment : BaseFragment<FragmentSettingDetailBinding>(
    FragmentSettingDetailBinding::inflate
) {
    private val args: SettingDetailFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: SettingDetailViewModel.SettingDetailFactory
    val viewModel: SettingDetailViewModel by viewModels{
        SettingDetailViewModel.provideFactory(viewModelFactory, args.settingDetailType)
    }

    override fun initListener() {
        binding.tbSetting.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handelEvent(event)
            }
        }
    }

    private fun handelEvent(event : SettingDetailViewModel.Event) {
        when (event) {
            is SettingDetailViewModel.Event.PrivacyPolicy -> {
                binding.tbSetting.title = getString(R.string.setting_privacy_policy)
                // todo 개인정보 보호정책 텍스트 설정
            }
            is SettingDetailViewModel.Event.TermsOfUse -> {
                binding.tbSetting.title = getString(R.string.setting_terms_of_use)
                // todo 이용약관 텍스트 설정
            }
            is SettingDetailViewModel.Event.OpenSource -> {
                binding.tbSetting.title = getString(R.string.setting_open_source)
                // todo 오픈소스 텍스트 설정
            }
        }
    }
}
