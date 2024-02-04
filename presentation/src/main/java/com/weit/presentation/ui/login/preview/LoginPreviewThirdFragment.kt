package com.weit.presentation.ui.login.preview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentLoginPreviewThirdBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.login.consent.LoginConsentDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginPreviewThirdFragment : BaseFragment<FragmentLoginPreviewThirdBinding>(
    FragmentLoginPreviewThirdBinding::inflate,
) {
    private var bottomSheetDialog: LoginConsentDialogFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCommentBottomSheet()
    }
    private fun initCommentBottomSheet() {
        if(bottomSheetDialog==null){
            bottomSheetDialog = LoginConsentDialogFragment()
        }
//        bottomSheetDialog?.setStyle(
//            DialogFragment.STYLE_NORMAL,
//            R.style.AppBottomSheetDialogTheme,
//        )

        if(bottomSheetDialog?.isAdded?.not() == true){
            binding.btnLoginPreviewThirdStart.setOnClickListener {
                bottomSheetDialog?.show(
                    requireActivity().supportFragmentManager,
                    LoginConsentDialogFragment.TAG,
                )
            }
        }

    }
    override fun initCollector() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomSheetDialog?.dismiss()
        bottomSheetDialog = null
    }
}
