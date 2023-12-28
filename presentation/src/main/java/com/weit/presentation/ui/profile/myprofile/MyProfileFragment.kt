package com.weit.presentation.ui.profile.myprofile

import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentMyProfileBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.FavoriteTopicAdapter
import com.weit.presentation.ui.profile.menu.ProfileMenuFragment
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>(
    FragmentMyProfileBinding::inflate,
) {

    private val viewModel: MyProfileViewModel by viewModels()
    private var profileMenuDialog: ProfileMenuFragment? = null
    private val myProfileLifeShotAdapter = MyProfileLifeShotAdapter(
        selectImage = { LifeShotEntity ->
            //TODO 확대샷
        }
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        //TODO 유저인생샷
            //블러처리
        initRecyclerView()
        //TODO 관심장소
        //TODO 즐겨찾기 여행일지
        //TODO 대표여행일지

    }

    override fun initListener() {
        binding.ivProfileUser.setOnClickListener {
            if (profileMenuDialog == null) {

                profileMenuDialog = ProfileMenuFragment { uri ->
                    Glide.with(binding.root)
                        .load(uri)
                        .into(binding.ivProfileUser)
                }

            }
            if (profileMenuDialog?.isAdded?.not() == true) {
                profileMenuDialog?.show(
                    requireActivity().supportFragmentManager,
                    ProfileMenuFragment.TAG,
                )
            }

        }

        binding.tvProfileLifeshotTitle.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToFragmentLifeShotPicker()
            findNavController().navigate(action)
        }

        binding.tvProfileMyCommunity.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToFragmentFeedMyActivity()
            findNavController().navigate(action)
        }
    }

    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextLifeShots()
            }
        }
    }

    private fun initRecyclerView(){
        binding.rvProfileLifeshot.run {
            addItemDecoration(
                SpaceDecoration(
                    resources,
                    rightDP = R.dimen.item_feed_comment_space,
                ),
            )
            addOnScrollListener(infinityScrollListener)
            adapter = myProfileLifeShotAdapter
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.lifeshots.collectLatest { lifeshots ->
                if(!lifeshots.isNullOrEmpty()){
                Glide.with(binding.root)
                    .load(lifeshots.first().imageUrl)
                    .into(binding.ivProfileBg)
                myProfileLifeShotAdapter.submitList(lifeshots)
                binding.ivProfileBg.setBlur(100)}
            }
        }
    }

    private fun handleEvent(event: MyProfileViewModel.Event) {

        when (event) {
            is MyProfileViewModel.Event.GetUserStatisticsSuccess -> {
                Glide.with(binding.root)
                    .load(event.user.profile.url)
                    .into(binding.ivProfileUser)
                binding.tvProfileNickname.text = event.user.nickname
                binding.tvProfileTotalOdyaCount.text = event.statistics.odyaCount.toString()
                binding.tvProfileTotalFollowingCount.text = event.statistics.followingsCount.toString()
                binding.tvProfileTotalFollowCount.text = event.statistics.followersCount.toString()
                val baseString=
                    getString(
                        R.string.profile_total_travel_count,
                        event.user.nickname,
                        event.statistics.travelPlaceCount,
                        event.statistics.travelJournalCount
                    )


                binding.tvProfileTotalTravelCount.text = HtmlCompat.fromHtml(
                    baseString,
                    HtmlCompat.FROM_HTML_MODE_COMPACT,
                )
            }
            else -> {}
        }
    }

}
