package com.weit.presentation.ui.login

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentLoginStepTwoBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginStepTwoFragment : BaseFragment<FragmentLoginStepTwoBinding>(
    FragmentLoginStepTwoBinding::inflate
){
    private val viewModel: LoginStepTwoViewModel by viewModels()

    private lateinit var genderArray: Array<String>;


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

//    private fun setDropdown(){
//        genderArray = resources.getStringArray(R.array.login_gender)
//        val genderArrayAdaper = ArrayAdapter<requireContext(), R.layout.>
//    }
}
