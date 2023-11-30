package com.weit.presentation.ui.mypage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentMyProfileBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyProfileBinding>(
    FragmentMyProfileBinding::inflate,
) {

    private val viewModel: MyPageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initCollector() {
    }
}
