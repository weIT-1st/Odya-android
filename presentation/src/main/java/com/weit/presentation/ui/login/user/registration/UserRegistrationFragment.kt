package com.weit.presentation.ui.login.user.registration

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.button.MaterialButtonToggleGroup.OnButtonCheckedListener
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentUserRegistrationBinding
import com.weit.presentation.model.GenderType
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class UserRegistrationFragment : BaseFragment<FragmentUserRegistrationBinding>(
    FragmentUserRegistrationBinding::inflate
) {

    @Inject
    lateinit var viewModelFactory: UserRegistrationViewModel.UsernameFactory

    private val args: UserRegistrationFragmentArgs by navArgs()

    private val viewModel: UserRegistrationViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewModelFactory.create(args.username) as T
            }
        }
    }

    private val datePicker by lazy {
        val dateSetListener = OnDateSetListener { _, year, month, dayOfMonth ->
            viewModel.setBirth(year, month, dayOfMonth)
            binding.etUserRegistrationBirth.setText(
                requireContext().getString(R.string.edit_text_birth, year, month + 1, dayOfMonth)
            )
        }
        DatePickerDialog(requireContext(), dateSetListener, DEFAULT_YEAR, DEFAULT_MONTH, DEFAULT_DAY)
    }

    private val genderCheckedListener by lazy {
        OnButtonCheckedListener { _, checkedId, _ ->
            if (checkedId == R.id.btn_user_registration_male) {
                viewModel.setGender(GenderType.MALE)
            } else {
                viewModel.setGender(GenderType.FEMALE)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initListener() {
        binding.etUserRegistrationBirth.setOnClickListener {
            datePicker.show()
        }
        binding.toggleUserRegistrationGender.addOnButtonCheckedListener(genderCheckedListener)
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.toggleUserRegistrationGender.removeOnButtonCheckedListener(genderCheckedListener)
    }

    private fun handleEvent(event: UserRegistrationViewModel.Event) {
        when (event) {
            UserRegistrationViewModel.Event.GenderNotSelected -> {
            }
            UserRegistrationViewModel.Event.RegistrationSuccess -> {
            }
            UserRegistrationViewModel.Event.RegistrationFailed -> {
            }
            UserRegistrationViewModel.Event.BirthNotSelected -> {
            }
            UserRegistrationViewModel.Event.NicknameIsEmpty -> {
            }
        }
    }

    companion object {
        private const val DEFAULT_YEAR = 2000
        private const val DEFAULT_MONTH = 0
        private const val DEFAULT_DAY = 1
    }
}
