package com.weit.presentation.ui.login.inputuserinfo

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentLoginInputUserInfoBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginInputUserInfoFragment : BaseFragment<FragmentLoginInputUserInfoBinding>(
    FragmentLoginInputUserInfoBinding::inflate,
) {

    private val viewModel: LoginInputUserInfoViewModel by viewModels()

    private val datePicker by lazy {
        val dateSetListener = OnDateSetListener { _, year, month, dayOfMonth ->
            viewModel.setBirth(year, month, dayOfMonth)
            binding.etLoginBirth.setText(
                requireContext().getString(R.string.edit_text_birth, year, month + 1, dayOfMonth),
            )
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
        highlightNickname()
    }

    override fun initListener() {
        binding.etLoginBirth.setOnClickListener {
            datePicker.show()
        }
    }

    private fun highlightNickname() {
        val nickname = viewModel.nickname
        val mainText: String = String.format(resources.getString(R.string.login_who_you), nickname)
        val spannableStringBuilder = SpannableStringBuilder(mainText)
        spannableStringBuilder.apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary)), 0, nickname.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.tvLoginBirthGender.text = spannableStringBuilder
    }

    private fun genderDropbox() {
        val genders = resources.getStringArray(R.array.login_gender)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_login_gender, genders)
        adapter.setDropDownViewResource(R.layout.dropdown_login_gender)
        binding.spinnerLoginGender.adapter = adapter
    }

    companion object {
        private const val DEFAULT_YEAR = 2000
        private const val DEFAULT_MONTH = 0
        private const val DEFAULT_DAY = 1
    }
}
