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

class FeedAdapter(
    private val navigateTravelLog: (Long) -> Unit,
    private val navigateFeedDetail: (Long) -> Unit,
    private val onFollowChanged: (Long, Boolean) -> Unit,
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
        fun bind(mayKnowFriend: Feed.MayknowFriendItem) {
            val data = mayKnowFriend.mayKnowFriendList
            val mayKnowFriendRecyclerView = binding.rvMayknowFriendSummary
            val mayKnowFriendAdapter = MayKnowFriendAdapter { position, userId, isChecked ->
                onFollowChanged(userId, isChecked)
            }
            mayKnowFriendAdapter.submitList(data)
            mayKnowFriendRecyclerView.adapter = mayKnowFriendAdapter
        }
    }

    inner class CommunityViewHolder(
        private val binding: ItemCommunityBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(feed: Feed.FeedItem) {
            binding.feed = feed

            if (feed.travelLog == null) {
                binding.viewCommunityTitle.visibility = View.INVISIBLE
                binding.ivCommunityBookmark.visibility = View.INVISIBLE
                binding.tvCommunityTitle.visibility = View.INVISIBLE
                binding.ivCommunityDirection.visibility = View.INVISIBLE
            }

            binding.viewCommunityContent.setOnClickListener {
                navigateFeedDetail(feed.feedId)
            }
            binding.btCommunityFollow.setOnClickListener {
                onFollowChanged(feed.userId, feed.followState)
            }
            binding.viewCommunityTitle.setOnClickListener {
                feed.travelLog?.let { log -> navigateTravelLog(log.travelLogId) }
            }
        }
    }

    companion object {
        private val Callback: DiffUtil.ItemCallback<Feed> =
            object : DiffUtil.ItemCallback<Feed>() {
                override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
                    return oldItem is Feed.FeedItem && newItem is Feed.FeedItem &&
                        oldItem.feedId == newItem.feedId
                }

                override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
