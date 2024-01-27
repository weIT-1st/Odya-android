package com.weit.presentation.ui.profile.preferences.update

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.databinding.FragmentUpdateUserInfoBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UpdateUserInfoFragment : BaseFragment<FragmentUpdateUserInfoBinding>(
    FragmentUpdateUserInfoBinding::inflate
) {
    val viewModel : UpdateUserInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initListener() {
        binding.tbUpdateInfo.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.user.collectLatest { user ->
                user?.let {
                    binding.etUpdateInfoNickname.hint = user.nickname
                    user.phoneNumber?.let {  phone ->
                        binding.etUpdateInfoPhone.hint = phone
                    }
                    user.email?.let { email ->
                        binding.etUpdateInfoEmail.hint = email
                    }
                }
            }
        }
    }
}
