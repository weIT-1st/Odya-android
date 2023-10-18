package com.weit.presentation.ui.login.inputuserinfo

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.weit.domain.model.GenderType
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentLoginInputUserInfoBinding
import com.weit.presentation.ui.MainActivity
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.login.user.registration.UserRegistrationViewModel
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

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: LoginInputUserInfoViewModel.Event) {
        when (event) {
            LoginInputUserInfoViewModel.Event.GenderNotSelected -> {
                sendSnackBar("성별이 비어있어요!")
            }
            LoginInputUserInfoViewModel.Event.RegistrationSuccess -> {
                moveToMain()
            }
            LoginInputUserInfoViewModel.Event.RegistrationFailed -> {
                sendSnackBar("많은 에러 케이스를 뚫고 그냥 실패했어요\n인터넷 연결이라도 확인해보세요")
            }
            LoginInputUserInfoViewModel.Event.BirthNotSelected -> {
                sendSnackBar("생년월일이 비어있어요!")
            }
            LoginInputUserInfoViewModel.Event.DuplicatedNickname -> {
                sendSnackBar("뭔가 중복이 났는데 아마 닉네임일 겁니다.")
            }
            LoginInputUserInfoViewModel.Event.GetStoredBirthFaild -> {
                sendSnackBar("생일을 불러오는데 실패했습니다.")
            }
            LoginInputUserInfoViewModel.Event.GetStoredUsernameFaild -> {
                sendSnackBar("닉네임을 불러오는데 실패했습니다.")
            }
            else -> {}
        }
    }

    private fun moveToMain() {
        requireActivity().run {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
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
        binding.spinnerLoginGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setGender(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    companion object {
        private const val DEFAULT_YEAR = 2000
        private const val DEFAULT_MONTH = 0
        private const val DEFAULT_DAY = 1
    }
}
