package com.weit.presentation.ui.feed.myactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.weit.presentation.R
import com.weit.presentation.databinding.BottomSheetPlaceSearchBinding
import com.weit.presentation.databinding.FragmentFeedMyActivityBinding
import com.weit.presentation.ui.feed.myactivity.comment.FeedMyActivityCommentFragment
import com.weit.presentation.ui.feed.myactivity.comment.FeedMyActivityCommentViewModel
import com.weit.presentation.ui.feed.myactivity.like.FeedMyActivityLikeFragment
import com.weit.presentation.ui.feed.myactivity.post.FeedMyActivityPostFragment
import com.weit.presentation.ui.searchplace.community.PlaceCommunityFragment
import com.weit.presentation.ui.searchplace.journey.PlaceJourneyFragment
import com.weit.presentation.ui.searchplace.review.PlaceReviewFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FeedMyActivityFragment(
) : BottomSheetDialogFragment() {

    private val viewModel: FeedMyActivityViewModel by viewModels()

    private var _binding: FragmentFeedMyActivityBinding? = null
    private val binding get() = _binding!!

    private val tabItem = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeedMyActivityBinding.inflate(inflater, container, false)
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTabViewPager()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun initTabViewPager() {
        val viewPager = binding.vpFeedMyActivity
        val tabLayout = binding.tlFeedMyActivity

        tabItem.add(tabPost, FeedMyActivityPostFragment())
        tabItem.add(tabComment, FeedMyActivityCommentFragment())
        tabItem.add(tabLike, FeedMyActivityLikeFragment())

        viewPager.apply {
            adapter = FeedMyActivityAdapter(this.findFragment(), tabItem)
            isUserInputEnabled = false
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                tabPost -> tab.text = getString(R.string.feed_my_activity_post)
                tabComment -> tab.text = getString(R.string.feed_my_activity_comment)
                tabLike -> tab.text = getString(R.string.feed_my_activity_like)
            }
        }.attach()
    }


//    private fun handelEvent(event: SearchPlaceBottomSheetViewModel.Event) {
//        when (event) {
//
//        }
//    }


    companion object {
        private const val tabPost = 0
        private const val tabComment = 1
        private const val tabLike = 2
    }
}
