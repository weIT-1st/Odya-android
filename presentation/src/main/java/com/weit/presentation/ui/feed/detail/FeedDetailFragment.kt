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
import com.weit.presentation.model.TravelLogInFeed
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.detail.CommentDialogFragment
import com.weit.presentation.ui.feed.detail.CommentDialogViewModel
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
//    private val viewModel: FeedDetailViewModel by viewModels()
    private val feedCommentAdapter = FeedCommentAdapter(
        updateItem = { position -> changeComment(position) },
        deleteItem = { position -> viewModel.deleteComment(position)},
    )
    private val feedTopicAdapter = FeedTopicAdapter()
    private var bottomSheetDialog: CommentDialogFragment? = null

    private fun changeComment(position:Int){
        binding.etFeedComment.setText(viewModel.commentList[position].content)
        viewModel.updateComment(position)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.feedId = args.feedId
        binding.vm = viewModel
        initCommentRecyclerView()
        binding.btCommunityFollow.setOnClickListener {
            viewModel.onFollowStateChange(binding.btCommunityFollow.isChecked)
        }
        binding.btnWriteComment.setOnClickListener {
            viewModel.registerAndUpdateComment()
        }
        binding.tbFeedDetail.setOnClickListener {
            viewModel.deleteFeed()
        }

        // TODO 좋아요
    }
    private fun showCommentBottomSheet(feed: FeedDetail) {
        if(bottomSheetDialog==null){
            bottomSheetDialog = CommentDialogFragment(feed)
        }
        if(bottomSheetDialog?.isAdded?.not() == true){
            binding.btnFeedCommentMore.setOnClickListener {
                bottomSheetDialog?.show(
                    requireActivity().supportFragmentManager,
                    CommentDialogFragment.TAG,
                )
            }
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
        binding.rvTopic.adapter = feedTopicAdapter
    }

    private fun navigateTravelLog(travelLogId: Long) {
        val action = FeedFragmentDirections.actionFragmentFeedToFragmentTravellog(travelLogId)
        findNavController().navigate(action)
    }

    override fun initListener() {
        binding.tbFeedDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun setTravelLog(log: TravelLogInFeed?) {
        if (log == null) {
            binding.includeTravelLog.layoutTravelLog.visibility = View.GONE
        } else {
            binding.includeTravelLog.layoutTravelLog.visibility = View.VISIBLE
            binding.includeTravelLog.log = log
            binding.includeTravelLog.layoutTravelLog.setOnClickListener {
                navigateTravelLog(log.travelLogId)
            }
        }
    }

    private fun handleEvent(event: FeedDetailViewModel.Event) {
        when (event) {
            is FeedDetailViewModel.Event.OnChangeFeed -> {
                setTravelLog(event.feed.travelLog)
                feedTopicAdapter.submitList(event.topics)
            }
            is FeedDetailViewModel.Event.OnChangeComments -> {
                feedCommentAdapter.submitList(event.defaultComments)
                if (event.remainingCommentsCount > 0) {
                    binding.btnFeedCommentMore.text =
                        getString(R.string.feed_detail_comment, event.remainingCommentsCount)
                }
                event.feed?.let { showCommentBottomSheet(it) }
            }
            is FeedDetailViewModel.Event.OnChangeFollowState -> {
                binding.btCommunityFollow.isChecked = event.followState
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
        super.onDestroyView()
        bottomSheetDialog?.dismiss()
        bottomSheetDialog = null
    }
}
