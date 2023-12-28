package com.weit.presentation.ui.profile.lifeshot

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentLifeShotBinding
import com.weit.presentation.model.profile.lifeshot.SelectLifeShotImageDTO
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.detail.menu.FeedDetailOtherMenuFragment
import com.weit.presentation.ui.feed.myactivity.FeedMyActivityImageAdapter
import com.weit.presentation.ui.post.datepicker.DatePickerDialogFragment
import com.weit.presentation.ui.profile.myprofile.MyProfileViewModel
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

@AndroidEntryPoint
class LifeShotPickerFragment() : BaseFragment<FragmentLifeShotBinding>(
    FragmentLifeShotBinding::inflate,
) {
    private val viewModel: LifeShotPickerViewModel by viewModels()

    private val imageAdapter = LifeShotImageAdapter{
        imageId, imageUri -> selectLifeShotImage(imageId,imageUri)
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvLifeshotImage.run {
            addOnScrollListener(infinityScrollListener)
            adapter = imageAdapter
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                findNavController().currentBackStackEntry?.savedStateHandle?.getStateFlow<String?>(
                    "placeName",
                    null
                )?.collect { placeName ->
                    if (!placeName.isNullOrEmpty()) {
                        viewModel.selectLifeShotPlace(placeName)
                    }
                }
            }
        }
    }

    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextImages()
            }
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.lifeshotImages.collectLatest { images ->
                imageAdapter.submitList(images)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: LifeShotPickerViewModel.Event) {

        when (event) {
            is LifeShotPickerViewModel.Event.onSelectCompleted -> {
                val direction =
                    LifeShotPickerFragmentDirections.actionFragmentLifeShotPickerToLifeShotDialogFragment(
                        selectImage = event.imageInfo,
                        placeName = event.placeName,
                    )
                findNavController().navigate(direction)
            }
            else -> {}
        }
    }

    private fun selectLifeShotImage(imageId: Long, imageUri:String) {
        viewModel.selectLifeShotImage(SelectLifeShotImageDTO(imageId,imageUri))
        val direction = LifeShotPickerFragmentDirections.actionFragmentLifeShotPickerToLifeShotDialogFragment(
            selectImage = SelectLifeShotImageDTO(imageId,imageUri),
            placeName = null
        )
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        binding.rvLifeshotImage.removeOnScrollListener(infinityScrollListener)
        binding.rvLifeshotImage.adapter = null
        super.onDestroyView()
    }
}
