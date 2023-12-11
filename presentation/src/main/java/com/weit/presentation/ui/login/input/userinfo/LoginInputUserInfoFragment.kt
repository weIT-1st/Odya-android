package com.weit.presentation.ui.login.input.userinfo

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentLoginInputUserInfoBinding
import com.weit.presentation.ui.MainActivity
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginInputUserInfoFragment : BaseFragment<FragmentLoginInputUserInfoBinding>(
    FragmentLoginInputUserInfoBinding::inflate,
) {

    private val viewModel: LoginInputUserInfoViewModel by viewModels()

    private val datePicker by lazy {
        val dateSetListener = OnDateSetListener { _, year, month, dayOfMonth ->
            viewModel.setBirth(year, month, dayOfMonth)
        }
        DatePickerDialog(
            requireContext(),
            dateSetListener,
            DEFAULT_YEAR,
            DEFAULT_MONTH,
            DEFAULT_DAY,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        genderDropbox()
    }

    override fun initListener() {
        binding.etLoginInputUserInfoBirth.setOnClickListener {
            datePicker.show()
        }

        binding.btnLoginInputUserInfoGoBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.nickname.collectLatest { nickname ->
                highlightNickname(nickname)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.birth.collectLatest { birth ->
                if (birth != null) {
                    binding.etLoginInputUserInfoBirth.setText(
                        requireContext().getString(
                            R.string.edit_text_birth,
                            birth.year,
                            birth.monthValue,
                            birth.dayOfMonth
                        ),
                    )
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun moveToMainLoading() {
        val action = LoginInputUserInfoFragmentDirections.actionLoginInputUserInfoFragmentToLoginLoadingFragment()
        findNavController().navigate(action)
    }

    private fun highlightNickname(nickname: String) {
        val mainText: String = String.format(resources.getString(R.string.login_who_you), nickname)
        val spannableStringBuilder = SpannableStringBuilder(mainText)
        spannableStringBuilder.apply {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary)),
                0,
                nickname.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.tvLoginInputUserInfoTitle.text = spannableStringBuilder
    }

    private fun genderDropbox() {
        val list = resources.getStringArray(R.array.login_gender).toList()

        binding.spinnerLoginInputUserInfoGender.adapter =
            GenderOptionSpinnerAdapter(requireContext(), R.layout.dropdown_login_gender, list)
        binding.spinnerLoginInputUserInfoGender.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    viewModel.setGender(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
    }

    private fun handleEvent(event: LoginInputUserInfoViewModel.Event) {
        when (event) {
            LoginInputUserInfoViewModel.Event.BirthNotSelected -> {
                sendSnackBar("생년월일이 제대로 입력되지 않았어요. 생년월일을 입력해주세요")
            }
            LoginInputUserInfoViewModel.Event.GenderNotSelected -> {
                sendSnackBar("성별이 제대로 입력되지 않았어요. 성별을 입력해주세")
            }
            LoginInputUserInfoViewModel.Event.NotReadyToRegister -> {}
            LoginInputUserInfoViewModel.Event.ReadyToRegister -> {
                moveToMainLoading()
            }
        }
    }

    companion object {
        private const val DEFAULT_YEAR = 2000
        private const val DEFAULT_MONTH = 0
        private const val DEFAULT_DAY = 1
    }
}
