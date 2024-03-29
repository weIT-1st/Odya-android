package com.weit.presentation.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemCommunityBinding
import com.weit.presentation.databinding.ItemMayknowFriendBinding
import com.weit.presentation.databinding.ItemPopularSpotBinding
import com.weit.presentation.model.Feed
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.Constants.DEFAULT_REACTION_COUNT

class FeedAdapter(
    private val navigateTravelLog: (Long) -> Unit,
    private val navigateFeedDetail: (Long,String) -> Unit,
    private val onFollowChanged: (Long) -> Unit,
    private val onLikeChanged: (Long) -> Unit,
    private val friendScrollListener: () -> Unit,
    private val journalScrollListener: () -> Unit,
) :
    ListAdapter<Feed, RecyclerView.ViewHolder>(
        Callback,
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_popular_spot -> PopularSpotViewHolder(
                ItemPopularSpotBinding.inflate(
                    inflater,
                    parent,
                    false,
                ),
            )

            R.layout.item_mayknow_friend -> MayKnowFriendViewHolder(
                ItemMayknowFriendBinding.inflate(
                    inflater,
                    parent,
                    false,
                ),
            )

            R.layout.item_community -> CommunityViewHolder(
                ItemCommunityBinding.inflate(
                    inflater,
                    parent,
                    false,
                ),
            )

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is PopularSpotViewHolder -> holder.bind(item as Feed.PopularTravelLogItem)
            is MayKnowFriendViewHolder -> holder.bind(item as Feed.MayknowFriendItem)
            is CommunityViewHolder -> holder.bind(item as Feed.FeedItem)
        }
    }

    override fun getItemViewType(position: Int) = when {
        getItem(position) is Feed.PopularTravelLogItem -> R.layout.item_popular_spot
        getItem(position) is Feed.MayknowFriendItem -> R.layout.item_mayknow_friend
        getItem(position) is Feed.FeedItem -> R.layout.item_community
        else -> throw IllegalArgumentException("Unknown view type")
    }

    inner class PopularSpotViewHolder(
        private val binding: ItemPopularSpotBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        init{
            binding.rvPopularSpotSummary.addOnScrollListener(infinityJournaldcrollListener)
        }
        fun bind(popularTravelLog: Feed.PopularTravelLogItem) {
            val data = popularTravelLog.popularTravelLogList
            val popularSpotRecyclerView = binding.rvPopularSpotSummary
            val popularSpotAdapter = PopularSpotAdapter { travelLogId ->
                navigateTravelLog(travelLogId)
            }
            popularSpotAdapter.submitList(data)
            popularSpotRecyclerView.adapter = popularSpotAdapter
        }
    }

    inner class MayKnowFriendViewHolder(
        private val binding: ItemMayknowFriendBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init{
            binding.rvMayknowFriendSummary.addOnScrollListener(infinityFriendScrollListener)
        }
        fun bind(mayKnowFriend: Feed.MayknowFriendItem) {
            val data = mayKnowFriend.mayKnowFriendList
            val mayKnowFriendRecyclerView = binding.rvMayknowFriendSummary
            val mayKnowFriendAdapter = MayKnowFriendAdapter { position, userId, isChecked ->
                onFollowChanged(userId)
            }
            mayKnowFriendAdapter.submitList(data)
            mayKnowFriendRecyclerView.adapter = mayKnowFriendAdapter

        }
    }

    private val infinityFriendScrollListener = object : InfinityScrollListener() {
        override fun loadNextPage() {
            friendScrollListener()
        }
    }

    private val infinityJournaldcrollListener = object : InfinityScrollListener() {
        override fun loadNextPage() {
            journalScrollListener()
        }
    }
    inner class CommunityViewHolder(
        private val binding: ItemCommunityBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(feed: Feed.FeedItem) {
            binding.feed = feed

            if (feed.travelJournalSimpleResponse == null) {
                binding.viewCommunityTitle.visibility = View.INVISIBLE
                binding.ivCommunityBookmark.visibility = View.INVISIBLE
                binding.tvCommunityTitle.visibility = View.INVISIBLE
                binding.ivCommunityDirection.visibility = View.INVISIBLE
            }
            binding.viewCommunityTitle.setOnClickListener {
                feed.travelJournalSimpleResponse?.let { log -> navigateTravelLog(log.travelJournalId) }
            }

            binding.viewCommunityContent.setOnClickListener {
                navigateFeedDetail(feed.communityId,feed.writer.nickname)
            }

            val followImage = if(feed.writer.isFollowing) R.drawable.bt_following else R.drawable.bt_follow
            binding.ivCommunityFollow.setImageResource(followImage)
            binding.ivCommunityFollow.setOnClickListener {
                onFollowChanged(feed.communityId)
            }

            val likeImage = if(feed.isUserLiked) R.drawable.ic_heart else R.drawable.ic_heart_blank
            binding.ivCommunityLike.setImageResource(likeImage)
            binding.ivCommunityLike.setOnClickListener {
                onLikeChanged(feed.communityId)
            }
        }
    }

    companion object {
        private val Callback: DiffUtil.ItemCallback<Feed> =
            object : DiffUtil.ItemCallback<Feed>() {
                override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
                    return oldItem is Feed.FeedItem && newItem is Feed.FeedItem &&
                        oldItem.communityId == newItem.communityId
                }

                override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
                    return oldItem is Feed.FeedItem && newItem is Feed.FeedItem &&
                            oldItem == newItem
                }
            }
    }
}
