package com.weit.presentation.ui.feed.myactivity

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFeedMyActivityBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.myactivity.comment.FeedMyActivityCommentFragment
import com.weit.presentation.ui.feed.myactivity.like.FeedMyActivityLikeFragment
import com.weit.presentation.ui.feed.myactivity.post.FeedMyActivityPostFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FeedMyActivityFragment: BaseFragment<FragmentFeedMyActivityBinding>(
    FragmentFeedMyActivityBinding::inflate,
)  {

    private val viewModel: FeedMyActivityViewModel by viewModels()

    private val tabItem = ArrayList<Fragment>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initListener() {
        binding.tbMyActivity.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.user.collectLatest { user ->
              val nickname = user?.nickname
                val mainText: String =
                    String.format(resources.getString(R.string.feed_my_activity_hint), nickname)
                val spannableStringBuilder = SpannableStringBuilder(mainText)
                spannableStringBuilder.apply {
                    setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.primary
                            )
                        ), 0, nickname?.length ?: 0, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                binding.tvMyActivityHint.text = spannableStringBuilder
                if (nickname != null) {
                    initTabViewPager(nickname)
                }
            }
        }
    }

    private fun initTabViewPager(nickname: String) {
        val viewPager = binding.vpFeedMyActivity
        val tabLayout = binding.tlFeedMyActivity

        tabItem.add(tabPost, FeedMyActivityPostFragment(nickname))
        tabItem.add(tabComment, FeedMyActivityCommentFragment(nickname))
        tabItem.add(tabLike, FeedMyActivityLikeFragment(nickname))

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
        setTabItemMargin(tabLayout,resources.getDimension(R.dimen.feed_my_activity_tab_margin).toInt())

    }

    private fun setTabItemMargin(tabLayout: TabLayout, marginEnd: Int) {
        for (i in 0 until 3) {
            val tabs = tabLayout.getChildAt(0) as ViewGroup
            for (i in 0 until tabs.childCount) {
                val tab = tabs.getChildAt(i)
                val lp = tab.layoutParams as LinearLayout.LayoutParams
                lp.marginEnd = marginEnd
                tab.layoutParams = lp
                tabLayout.requestLayout()
            }
        }
    }


    companion object {
        private const val tabPost = 0
        private const val tabComment = 1
        private const val tabLike = 2
    }
}
