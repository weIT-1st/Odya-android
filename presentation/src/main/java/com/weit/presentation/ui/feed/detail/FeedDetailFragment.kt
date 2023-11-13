package com.weit.presentation.ui.feed

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.CommunityContent
import com.weit.domain.model.community.CommunityTravelJournal
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFeedDetailBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.detail.CommentDialogFragment
import com.weit.presentation.ui.feed.detail.FeedCommentAdapter
import com.weit.presentation.ui.feed.detail.FeedDetailViewModel
import com.weit.presentation.ui.feed.detail.FeedTopicAdapter
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FeedDetailFragment : BaseFragment<FragmentFeedDetailBinding>(
    FragmentFeedDetailBinding::inflate,
) {
    private val args: FeedDetailFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: FeedDetailViewModel.FeedDetailFactory

    private val viewModel: FeedDetailViewModel by viewModels {
        FeedDetailViewModel.provideFactory(viewModelFactory,args.feedId)
    }
    private val feedCommentAdapter = FeedCommentAdapter(
        updateItem = { position -> changeComment(position) },
        deleteItem = { position -> viewModel.deleteComment(position)},
    )
    private var bottomSheetDialog: CommentDialogFragment? = null
    private val feedImageAdapter = FeedImageAdapter()

    private fun changeComment(position:Int){
        viewModel.updateComment(position)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initCommentRecyclerView()
        initListener()
    }

    private fun initCommentRecyclerView() {
        binding.rvFeedComment.run {
            addItemDecoration(
                SpaceDecoration(
                    resources,
                    topDP = R.dimen.item_feed_comment_space,
                    bottomDP = R.dimen.item_feed_comment_space,
                ),
            )
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
            adapter = feedCommentAdapter
        }
        binding.vpCommunityImages.adapter = feedImageAdapter

    }

    private fun showCommentBottomSheet() {
        if(bottomSheetDialog==null){
            bottomSheetDialog = CommentDialogFragment(args.feedId)
        }
    }


    private fun navigateTravelLog(travelLogId: Long) {
        val action = FeedFragmentDirections.actionFragmentFeedToFragmentTravellog(travelLogId)
        findNavController().navigate(action)
    }

    override fun initListener() {
        binding.tbFeedDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnWriteComment.setOnClickListener {
            viewModel.registerAndUpdateComment()
        }
        binding.tbFeedDetail.setOnClickListener {
            viewModel.deleteFeed()
        }
        binding.btnFeedCommentMore.setOnClickListener {
            if (bottomSheetDialog?.isAdded?.not() == true) {
                bottomSheetDialog?.show(
                    requireActivity().supportFragmentManager,
                    CommentDialogFragment.TAG,
                )
            }
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun setTravelLog(log: CommunityTravelJournal?) {
        if (log == null) {
            binding.includeTravelLog.layoutTravelLog.visibility = View.GONE
        } else {
            binding.includeTravelLog.layoutTravelLog.visibility = View.VISIBLE
            binding.includeTravelLog.log = log
            binding.includeTravelLog.layoutTravelLog.setOnClickListener {
                navigateTravelLog(log.travelJournalId)
            }
        }
    }

    private fun handleEvent(event: FeedDetailViewModel.Event) {
        when (event) {
            is FeedDetailViewModel.Event.OnChangeFeed -> {
                val followImage = if(event.feed.writer.isFollowing) R.drawable.bt_following else R.drawable.bt_follow
                binding.ivCommunityFollow.setImageResource(followImage)

                val imageResource = if(event.feed.isUserLiked) R.drawable.ic_heart else R.drawable.ic_heart_blank
                binding.ivCommunityLike.setImageResource(imageResource)

                feedImageAdapter.submitList(event.feedImages)
                binding.tvTopic.text = getString(R.string.feed_detail_topic, event.feed.topic?.topicWord)
                binding.btnFeedCommentMore.text =
                    getString(R.string.feed_detail_comment, event.feed.communityCommentCount)
                setTravelLog(event.feed.travelJournal)
            }
            is FeedDetailViewModel.Event.OnChangeComments -> {
                feedCommentAdapter.submitList(event.defaultComments)
                showCommentBottomSheet()
            }

            is FeedDetailViewModel.Event.DeleteCommunitySuccess -> {
                findNavController().popBackStack()
            }

            is FeedDetailViewModel.Event.InvalidRequestException -> {
                sendSnackBar("정보를 제대로 입력하십시오")
            }

            is FeedDetailViewModel.Event.InvalidTokenException -> {
                sendSnackBar("유효하지 않은 토큰입니다")
            }

            is FeedDetailViewModel.Event.NotHavePermissionException -> {
                sendSnackBar("당신 권한이 없어요")
            }

            is FeedDetailViewModel.Event.UnknownException -> {
                sendSnackBar("알 수 없는 에러 발생")
            }

            is FeedDetailViewModel.Event.ExistedFollowingIdException -> {
                sendSnackBar("이미 팔로우 중입니다")
            }

            else -> {}
        }
    }

    override fun onDestroyView() {
//                bottomSheetDialog?.dismiss()
//        bottomSheetDialog = null
        binding.rvFeedComment.adapter = null
        super.onDestroyView()
    }
}
