package com.weit.presentation.ui.feed

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
import com.weit.presentation.ui.feed.detail.CommentDialogFragment
import com.weit.presentation.ui.feed.detail.FeedCommentAdapter
import com.weit.presentation.ui.feed.detail.menu.FeedDetailMyMenuFragment
import com.weit.presentation.ui.feed.detail.FeedDetailViewModel
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
        binding.etFeedComment.setText(viewModel.commentList[position].content)
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
        if(commentDialog==null){
            commentDialog = CommentDialogFragment(args.feedId)

        }
            binding.btnFeedCommentMore.setOnClickListener {
                if(commentDialog?.isAdded?.not() == true){
                    commentDialog?.show(
                    requireActivity().supportFragmentManager,
                    CommentDialogFragment.TAG,
                )
            }
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
        binding.btCommunityFollow.setOnClickListener {
            viewModel.onFollowStateChange(binding.btCommunityFollow.isChecked)
        }
        binding.btnWriteComment.setOnClickListener {
            viewModel.registerAndUpdateComment()
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
                        otherMenuDialog = FeedDetailOtherMenuFragment(args.feedId)

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
                val uris = event.feed.communityContentImages.map{
                    it.imageUrl
                }
                feedImageAdapter.submitList(uris)
                binding.tvTopic.text = getString(R.string.feed_detail_topic, event.feed.topic?.topicWord)

                setTravelLog(event.feed.travelJournal)
            }
            is FeedDetailViewModel.Event.OnChangeComments -> {
                feedCommentAdapter.submitList(event.defaultComments)

                if(event.remainingCommentsCount == 0){
                    binding.btnFeedCommentMore.visibility = View.GONE
                }else{
                    binding.btnFeedCommentMore.visibility = View.VISIBLE
                    binding.btnFeedCommentMore.text =
                        getString(R.string.feed_detail_comment, event.remainingCommentsCount)
                }
                showCommentBottomSheet()
            }
            is FeedDetailViewModel.Event.OnChangeFollowState -> {
                binding.btCommunityFollow.isChecked = event.followState
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
        super.onDestroyView()
//        commentDialog?.dismiss()
//        commentDialog = null
    }
}
