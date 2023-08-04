package com.weit.presentation.ui.login

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
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

    private val datePicker by lazy {
        val dateSetListener = OnDateSetListener{_, year, month, dayOfMonth ->
            viewModel.setBirth(year, month, dayOfMonth)
            binding.etLoginBirth.setText(
                requireContext().getString(R.string.edit_text_birth, year, month + 1, dayOfMonth)
            )
        }
        DatePickerDialog(requireContext(), dateSetListener, DEFAULT_YEAR, DEFAULT_MONTH, DEFAULT_DAY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

    }

    override fun initListener() {
        binding.etLoginBirth.setOnClickListener{
            datePicker.show()
        }
    }

    companion object {
        private const val DEFAULT_YEAR = 2000
        private const val DEFAULT_MONTH = 0
        private const val DEFAULT_DAY = 1
    }

}
