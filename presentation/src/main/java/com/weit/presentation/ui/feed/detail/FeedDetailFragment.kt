package com.weit.presentation.ui.feed

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFeedDetailBinding
import com.weit.presentation.model.FeedDetail
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.detail.CommentDialogFragment
import com.weit.presentation.ui.feed.detail.FeedCommentAdapter
import com.weit.presentation.ui.feed.detail.FeedDetailViewModel
import com.weit.presentation.ui.util.Constants.DEFAULT_REACTION_COUNT
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FeedDetailFragment : BaseFragment<FragmentFeedDetailBinding>(
    FragmentFeedDetailBinding::inflate,
) {

    private val viewModel: FeedDetailViewModel by viewModels()
    private val args: FeedDetailFragmentArgs by navArgs()
    private val feedCommentAdapter = FeedCommentAdapter()
    private val bottomSheetDialog = CommentDialogFragment()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
//        Logger.t("MainTest").i("feedDetail에서 args${args.feedId}")
        initCommentRecyclerView()
        initCommentBottomSheet()
        binding.btnWriteComment.setOnClickListener {
            viewModel.registerComment()
        }
        binding.btCommunityFollow.setOnClickListener {
            viewModel.onFollowStateChange(binding.btCommunityFollow.isChecked)
        }

        // TODO 좋아요
    }
    private fun initCommentBottomSheet() {
        bottomSheetDialog.setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.AppBottomSheetDialogTheme,
        )

        binding.btnFeedCommentMore.setOnClickListener {
            bottomSheetDialog.show(
                requireActivity().supportFragmentManager,
                CommentDialogFragment.TAG,
            )
        }
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
    }

    private fun navigateTravelLog(travelLogId: Long) {
        val action = FeedFragmentDirections.actionFragmentFeedToFragmentTravellog(travelLogId)
        findNavController().navigate(action)
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun setFeedDetail(feed: FeedDetail) {
        binding.feed = feed

        if (feed.travelLog == null) {
            binding.includeTravelLog.layoutTravelLog.visibility = View.GONE
        } else {
            binding.includeTravelLog.log = feed.travelLog
            binding.includeTravelLog.layoutTravelLog.setOnClickListener {
                navigateTravelLog(feed.travelLog.travelLogId)
            }
        }

        if (feed.commentNum > DEFAULT_REACTION_COUNT) {
            binding.tvCommunityReply.text =
                binding.root.context.getString(
                    R.string.feed_reaction_over_count,
                    DEFAULT_REACTION_COUNT,
                )
        } else {
            binding.tvCommunityReply.text =
                binding.root.context.getString(
                    R.string.feed_reaction_count,
                    feed.commentNum,
                )
        }

        if (feed.commentNum > DEFAULT_REACTION_COUNT) {
            binding.tvCommunityHeart.text =
                binding.root.context.getString(
                    R.string.feed_reaction_over_count,
                    DEFAULT_REACTION_COUNT,
                )
        } else {
            binding.tvCommunityHeart.text =
                binding.root.context.getString(R.string.feed_reaction_count, feed.likeNum)
        }
    }

    private fun handleEvent(event: FeedDetailViewModel.Event) {
        when (event) {
            is FeedDetailViewModel.Event.OnChangeFeed -> {
                setFeedDetail(event.feed)
                feedCommentAdapter.submitList(event.defaultComments)
                if (event.remainingCommentsCount > 0) {
                    binding.btnFeedCommentMore.text =
                        getString(R.string.feed_detail_comment, event.remainingCommentsCount)
                }
                bottomSheetDialog.comments = event.comments
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

            is FeedDetailViewModel.Event.CreateFollowSuccess -> {
                sendSnackBar("팔로우 성공")
            }

            is FeedDetailViewModel.Event.DeleteFollowSuccess -> {
                sendSnackBar("팔로우 해제")
            }

            else -> {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
