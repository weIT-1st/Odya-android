package com.weit.presentation.ui.login.genderbirth

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentLoginGenderBirthBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.login.user.registration.UserRegistrationFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginGenderBirthFragment : BaseFragment<FragmentLoginGenderBirthBinding>(
    FragmentLoginGenderBirthBinding::inflate
){

    private val viewModel: LoginGenderBirthViewModel by viewModels()


    private val datePicker by lazy{
        val dateSetListener = OnDateSetListener{_, year, month, dayOfMonth ->
            viewModel.setBirth(year, month, dayOfMonth)
            binding.etLoginBirth.setText(
                requireContext().getString(R.string.edit_text_birth, year, month + 1, dayOfMonth)
            )
        }
        DatePickerDialog(requireContext(), dateSetListener,
            DEFAULT_YEAR, DEFAULT_MONTH, DEFAULT_DAY)
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
