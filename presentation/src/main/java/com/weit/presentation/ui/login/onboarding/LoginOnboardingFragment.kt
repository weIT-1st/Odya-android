package com.weit.presentation.ui.login.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentLoginOnboardingBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginOnboardingFragment: BaseFragment<FragmentLoginOnboardingBinding>(
    FragmentLoginOnboardingBinding::inflate
) {
    private val viewModel: LoginOnboardingViewModel by viewModels()
    private val loginOnboardingAdapter = LoginOnboardingAdapter()
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.changeCurrentPage(position)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLoginOnboardingViewPager()
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.loginOnboardingContents.collectLatest {
                loginOnboardingAdapter.submitList(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.currentPage.collectLatest {current ->
                if (current == loginOnboardingAdapter.itemCount - 1) {
                    binding.btnLoginOnboardingGoNext.apply {
                        text = context.getText(R.string.login_start_odya)
                        setOnClickListener {
                            moveToConsent()
                        }
                    }
                } else {
                    binding.btnLoginOnboardingGoNext.apply {
                        text = context.getText(R.string.go_next_step)
                        setOnClickListener{
                            binding.viewPagerLoginOnbarding.currentItem = current + 1
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.viewPagerLoginOnbarding.apply {
            adapter = null
            unregisterOnPageChangeCallback(pageChangeCallback)
        }
        super.onDestroyView()
    }

    private fun initLoginOnboardingViewPager(){
        binding.viewPagerLoginOnbarding.apply {
            adapter = loginOnboardingAdapter
            registerOnPageChangeCallback(pageChangeCallback)
        }
        TabLayoutMediator(binding.tabLayoutLoginOnboarding, binding.viewPagerLoginOnbarding){ _, _ -> }.attach()
    }

    private fun moveToConsent(){
//        val action = LoginOnboardingFragmentDirections.actionLoginOnboardingFragmentToLoginConsentDeviceFragment()
//        findNavController().navigate(action)
    }
}
