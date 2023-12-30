package com.weit.presentation.ui.feed.detail

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.weit.domain.model.community.CommunityTravelJournal
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFeedDetailBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.FeedFragmentDirections
import com.weit.presentation.ui.feed.FeedImageAdapter
import com.weit.presentation.ui.feed.detail.menu.FeedDetailMyMenuFragment
import com.weit.presentation.ui.feed.detail.menu.FeedDetailOtherMenuFragment
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
    private var commentDialog: CommentDialogFragment? = null
    private var myMenuDialog: FeedDetailMyMenuFragment? = null
    private var otherMenuDialog: FeedDetailOtherMenuFragment? = null

    private val feedImageAdapter = FeedImageAdapter()

    private fun changeComment(position:Int){
        viewModel.updateComment(position)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initCommentRecyclerView()
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
        if(commentDialog == null){
            commentDialog = CommentDialogFragment(args.feedId)
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
        binding.btnFeedCommentMore.setOnClickListener {
            if (commentDialog?.isAdded?.not() == true) {
                commentDialog?.show(
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
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.isWriter.collectLatest { isWriter ->
                initMenu(isWriter)
            }
        }
    }

    private fun initMenu(isWriter: Boolean) {
        binding.tbFeedDetail.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.feed_detail_menu) {
                if (isWriter) {
                    if (myMenuDialog == null) {
                        myMenuDialog = FeedDetailMyMenuFragment(args.feedId)

                    }
                    if (myMenuDialog?.isAdded?.not() == true) {
                        myMenuDialog?.show(
                            requireActivity().supportFragmentManager,
                            FeedDetailMyMenuFragment.TAG,
                        )
                    }
                } else {
                    if (otherMenuDialog == null) {
                        otherMenuDialog = FeedDetailOtherMenuFragment(args.feedId,args.nickname?:"")

                    }
                    if (otherMenuDialog?.isAdded?.not() == true) {
                        otherMenuDialog?.show(
                            requireActivity().supportFragmentManager,
                            FeedDetailOtherMenuFragment.TAG,
                        )
                    }
                }
            }
            true
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
                binding.tvTopic.visibility = if(event.feed.topic==null) View.GONE else View.VISIBLE
                if(event.feed.topic != null){
                binding.tvTopic.text = getString(R.string.feed_detail_topic, event.feed.topic?.topicWord)}

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
        }
    }

    override fun onDestroyView() {
        binding.rvFeedComment.adapter = null
        super.onDestroyView()
//        commentDialog?.dismiss()
//        commentDialog = null

    }
}
