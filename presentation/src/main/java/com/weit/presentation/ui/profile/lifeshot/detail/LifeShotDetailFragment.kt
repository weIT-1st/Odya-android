package com.weit.presentation.ui.profile.lifeshot.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentLifeShotDetailBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.FeedImageAdapter
import com.weit.presentation.ui.feed.FeedPostFragmentArgs
import com.weit.presentation.ui.profile.lifeshot.LifeShotPickerViewModel
import com.weit.presentation.ui.profile.lifeshot.detail.menu.LifeShotDetailMenuFragment
import com.weit.presentation.ui.profile.lifeshot.detail.menu.LifeShotDetailMenuViewModel
import com.weit.presentation.ui.profile.menu.ProfileMenuFragment
import com.weit.presentation.ui.util.Constants.DEFAULT_DATA_SIZE
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class LifeShotDetailFragment() : BaseFragment<FragmentLifeShotDetailBinding>(
    FragmentLifeShotDetailBinding::inflate,
) {
    private val args: LifeShotDetailFragmentArgs by navArgs()

    private val lifeShotImageAdapter = LifeShotImageAdapter()
    private var lifeShotDetailMenuFragment: LifeShotDetailMenuFragment? = null

    @Inject
    lateinit var viewModelFactory: LifeShotDetailViewModel.LifeShotDetailFactory

    private val viewModel: LifeShotDetailViewModel by viewModels {
        LifeShotDetailViewModel.provideFactory(
            viewModelFactory,
            args.images.toList(),
            args.position,
            args.lifeshotRequestInfo
        )
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.transformImage(position)
            if(position%DEFAULT_DATA_SIZE==0){
                viewModel.loadNextLifeShots()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivLifeshotBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.vpLifeshot.adapter = lifeShotImageAdapter

        binding.vpLifeshot.registerOnPageChangeCallback(pageChangeCallback)

        binding.ivLifeshotMenu.setOnClickListener {
            if (lifeShotDetailMenuFragment?.isAdded?.not() == true) {
                lifeShotDetailMenuFragment?.show(
                    requireActivity().supportFragmentManager,
                    "lifeShotDetailMenuFragment",
                )
            }
        }
    }


    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.lifeshots.collectLatest { images ->
                lifeShotImageAdapter.submitList(images)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: LifeShotDetailViewModel.Event) {

        when (event) {
            is LifeShotDetailViewModel.Event.GetUserStatisticsSuccess -> {
                binding.tvLifeshotCount.text =
                    getString(R.string.lifeshot_count, event.currentPosition+1, event.totalCount)
                binding.vpLifeshot.currentItem = args.position
            }
            is LifeShotDetailViewModel.Event.OnTransformImage -> {
                binding.tvLifeshotCount.text =
                    getString(R.string.lifeshot_count, event.currentPosition + 1, event.totalCount)
                lifeShotDetailMenuFragment = LifeShotDetailMenuFragment(
                    imageId = event.imageId,
                    onComplete = { viewModel.onDeleteCompeleted(event.currentPosition) })
            }
            is LifeShotDetailViewModel.Event.OnDeleteSuccess -> {
                binding.tvLifeshotCount.text =
                        getString(R.string.lifeshot_count, 1, event.totalCount)
                binding.vpLifeshot.currentItem = 0
            }
            else -> {}
        }
    }


    override fun onDestroyView() {

        super.onDestroyView()
    }
}
