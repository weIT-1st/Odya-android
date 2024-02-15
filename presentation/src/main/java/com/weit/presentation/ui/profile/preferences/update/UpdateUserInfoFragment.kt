package com.weit.presentation.ui.profile.preferences.update

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentUpdateUserInfoBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class UpdateUserInfoFragment : BaseFragment<FragmentUpdateUserInfoBinding>(
    FragmentUpdateUserInfoBinding::inflate
) {
    val viewModel: UpdateUserInfoViewModel by viewModels()

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val phoneAuthCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("jomi", "VerificationCompleted")
                sendSnackBar("1분안에 인증을 완료하세요")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("jomi", "onVerificationFailed : $e")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("jomi", "onCodeSent")
                viewModel.settingPhonePhoneAuthCredential(verificationId)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initListener() {
        binding.tbUpdateInfo.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnUpdateInfoAddress.setOnClickListener {
            viewModel.sendEmailAuth()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.user.collectLatest { user ->
                user?.let {
                    binding.etUpdateInfoNickname.hint = user.nickname
                    user.phoneNumber?.let { phone ->
                        binding.etUpdateInfoPhone.hint = phone
                    }
                    user.email?.let { email ->
                        binding.etUpdateInfoEmail.hint = email
                    }
                }
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.newNickname.collectLatest {
                if (viewModel.checkIsGoodNickname()) {
                    binding.tvUpdateInfoNicknameDetail.text = requireContext().getText(R.string.update_info_nickname_safe)
                    binding.tvUpdateInfoNicknameDetail.setTextColor(ContextCompat.getColor(requireContext(), R.color.safe))
                    binding.tlUpdateInfoNickname.setEndIconDrawable(R.drawable.ic_x_safe)
                    binding.tlUpdateInfoNickname.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.safe)))
                } else {
                    binding.tvUpdateInfoNicknameDetail.setTextColor(ContextCompat.getColor(requireContext(), R.color.warning))
                    binding.tlUpdateInfoNickname.setEndIconDrawable(R.drawable.ic_x_warning)
                    binding.tlUpdateInfoNickname.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.warning)))
                }
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.newPhoneNum.collectLatest {

                if (viewModel.checkIsGoodPhoneNum()) {
                    binding.tlUpdateInfoPhone.setEndIconDrawable(R.drawable.ic_x_safe)
                    binding.tlUpdateInfoPhone.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.safe)))
                } else {
                    binding.tlUpdateInfoPhone.setEndIconDrawable(R.drawable.ic_x_warning)
                    binding.tlUpdateInfoPhone.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.warning)))
                }

            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.phoneAuthNum.collectLatest {
                viewModel.checkIsSameId()
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.isSameId.collectLatest {
                if (it) {
                    binding.tlUpdateInfoCertificationNumber.setEndIconDrawable(R.drawable.ic_x_safe)
                    binding.tlUpdateInfoCertificationNumber.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.safe)))
                } else {
                    binding.tlUpdateInfoCertificationNumber.setEndIconDrawable(R.drawable.ic_x_warning)
                    binding.tlUpdateInfoCertificationNumber.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.warning)))
                }
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun sendPhoneAuth(phoneNum: String) {
        val optionCompact = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNum)
            .setActivity(requireActivity())
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(phoneAuthCallbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(optionCompact)
    }

    private fun checkPhoneAuth(credentials: AuthCredential) {
        firebaseAuth.signInWithCredential(credentials)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("jomi", "signInWithCredential: Success")
                } else {
                    Log.d("jomi", "signInWithCredential: fail ${task.exception}")
                }
            }
    }

    private fun sendSignLinkToEmail(email: String, actionCodeSettings: ActionCodeSettings) {
        firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("jomi", "Email sent")
//                    val credentials = EmailAuthProvider.getCredentialWithLink(email, email)
//                    viewModel.checkEmailAuth(credentials)
                } else {
                    Log.d("jomi", "Email sent fail : ${task.result}")
                }
            }
    }

    private fun checkEmailAuth(credentials: AuthCredential) {
        firebaseAuth.currentUser!!.linkWithCredential(credentials)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModel.updateUserPhoneNum()
                } else {
                    sendPhoneAuth("전화 번호 인증에 실패 했습니다. 다시 시도해 주세요")
                }
            }
    }

    private fun handleEvent(event: UpdateUserInfoViewModel.Event) {
        when (event) {

            is UpdateUserInfoViewModel.Event.SendPhoneAuth -> {
                sendPhoneAuth(event.phoneNum)
            }
            is UpdateUserInfoViewModel.Event.CheckPhoneAuth -> {
                checkPhoneAuth(event.credentials)
            }

            is UpdateUserInfoViewModel.Event.SendEmailAuth -> {
                val actionCodeSettings = FirebaseCommunication.actionCodeSettings
                sendSignLinkToEmail(event.email, actionCodeSettings)
            }
            is UpdateUserInfoViewModel.Event.CheckEmailAuth -> {
                checkEmailAuth(event.credentials)
            }

            UpdateUserInfoViewModel.Event.SuccessUpdateNickname -> {
                sendSnackBar("닉네임 변경이 완료 되었습니다.")
            }
            UpdateUserInfoViewModel.Event.SuccessUpdatePhone -> {
                sendSnackBar("전화번호 수정이 완료 되었습니다.")
            }
            UpdateUserInfoViewModel.Event.IsNotGoodPhoneNum -> {
                sendSnackBar("휴대폰 번호를 확인해 주세요")
            }
            else -> {}
        }
    }
}
