package com.weit.presentation.ui.profile.lifeshot

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
import com.weit.presentation.databinding.FragmentDatePickerBinding
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.model.profile.lifeshot.SelectLifeShotImageDTO
import com.weit.presentation.ui.base.BaseDialogFragment
import com.weit.presentation.ui.feed.detail.CommentDialogViewModel
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
class LifeShotDialogFragment(
) : BaseDialogFragment<DialogLifeShotBinding>(
    DialogLifeShotBinding::inflate,
) {

    @Inject
    lateinit var viewModelFactory: LifeShotDialogViewModel.LifeShotFactory

    private val viewModel: LifeShotDialogViewModel by viewModels {
        LifeShotDialogViewModel.provideFactory(viewModelFactory,args.selectImage,args.placeName)
    }
    private val args: LifeShotDialogFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!args.placeName.isNullOrEmpty()){
            binding.btnLifeshotPlace.text = args.placeName
        }
    }

    override fun initListener() {
        binding.btnLifeshotPlace.setOnClickListener {
            val direction = LifeShotDialogFragmentDirections.actionLifeShotDialogFragmentToLifeShotSelectPlaceFragment()
            findNavController().navigate(direction)
        }
        binding.btnLifeshotClose.setOnClickListener { dismiss() }
        binding.btnLifeshotRegister.setOnClickListener { viewModel.registerLifeShot() }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collect { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.lifeshotImage.collect { uri ->
                    Glide.with(binding.root)
                        .load(uri)
                        .into(binding.ivLifeshot)
            }
        }
    }

    private fun handleEvent(event: LifeShotDialogViewModel.Event) {
        when (event) {
            is LifeShotDialogViewModel.Event.OnComplete -> {
                val direction =
                    LifeShotDialogFragmentDirections.actionLifeShotDialogFragmentToFragmentMypage()
                findNavController().navigate(direction)
            }

            else -> {}
        }
    }


}
