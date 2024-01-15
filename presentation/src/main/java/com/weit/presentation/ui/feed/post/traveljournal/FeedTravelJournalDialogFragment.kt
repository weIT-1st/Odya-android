package com.weit.presentation.ui.feed.post.traveljournal

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.weit.presentation.R
import com.weit.presentation.databinding.DialogLifeShotBinding
import com.weit.presentation.databinding.DialogNoJournalBinding
import com.weit.presentation.databinding.DialogUpdateJournalVisibilityBinding
import com.weit.presentation.databinding.FragmentDatePickerBinding
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.model.profile.lifeshot.SelectLifeShotImageDTO
import com.weit.presentation.ui.base.BaseDialogFragment
import com.weit.presentation.ui.feed.detail.CommentDialogViewModel
import com.weit.presentation.ui.post.selectplace.SelectPlaceViewModel
import com.weit.presentation.ui.post.travellog.PostTravelLogFragmentArgs
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class FeedTravelJournalDialogFragment(
    private val onComplete: (Unit) -> Unit
) : BaseDialogFragment<DialogUpdateJournalVisibilityBinding>(
    DialogUpdateJournalVisibilityBinding::inflate,
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDialogVisibilityCancel.setOnClickListener { dismiss() }
        binding.btnDialogVisibilityComplete.setOnClickListener {
            dismiss()
            onComplete(Unit)  }
    }

}
