package com.weit.presentation.ui.login

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class LoginStepAdapter(
    fragmentActivity: LoginMainFragment
): FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> LoginStepOneFragment()
            1 -> LoginStepTwoFragment()
            2 -> LoginStepThreeFragment()
            else -> LoginStepFourFragment()
        }
    }
}