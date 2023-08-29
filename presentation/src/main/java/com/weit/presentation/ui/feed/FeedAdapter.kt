package com.weit.presentation.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
                    false
                )
            )

            R.layout.item_mayknow_friend -> MayKnowFriendViewHolder(
                ItemMayknowFriendBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )

            R.layout.item_community -> CommunityViewHolder(
                ItemCommunityBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is PopularSpotViewHolder -> holder.bind(item as Feed.PopularTravelLogItem)
            is MayKnowFriendViewHolder -> holder.bind(item as Feed.MayknowFriendItem)
            is CommunityViewHolder -> {
                val feed = item as Feed.FeedItem
                holder.bind(feed)
                holder.content.setOnClickListener {
                    navigateFeedDetail(item.feedId)
                }
                holder.follow.setOnClickListener {
                    onFollowChanged(feed.userId, feed.followState)
                }
                holder.travelLog.setOnClickListener {
                    feed.travelLogId?.let { id -> navigateTravelLog(id) }
                }
            }
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
            val mayKnowFriendAdapter = MayKnowFriendAdapter { userId, isChecked ->
                onFollowChanged(userId, isChecked)
            }
            mayKnowFriendAdapter.submitList(data)
            mayKnowFriendRecyclerView.adapter = mayKnowFriendAdapter
        }
    }

    inner class CommunityViewHolder(
        private val binding: ItemCommunityBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        val travelLog = binding.viewCommunityTitle
        val follow = binding.btCommunityFollow
        val content = binding.viewCommunityContent

        fun bind(feed: Feed.FeedItem) {
            if (feed.travelLogId == null) {
                travelLog.visibility = View.INVISIBLE
                binding.ivCommunityBookmark.visibility = View.INVISIBLE
                binding.tvCommunityTitle.visibility = View.INVISIBLE
                binding.ivCommunityDirection.visibility = View.INVISIBLE
            }
            // !!을 써도 되나..
            if (feed.commentNum!! > DEFAULT_REACTION_COUNT) {
                binding.tvCommunityReply.text =
                    binding.root.context.getString(R.string.feed_reaction_count)
            } else {
                binding.tvCommunityReply.text = feed.commentNum.toString()
            }

            if (feed.commentNum!! > DEFAULT_REACTION_COUNT) {
                binding.tvCommunityHeart.text =
                    binding.root.context.getString(R.string.feed_reaction_count)
            } else {
                binding.tvCommunityHeart.text = feed.likeNum.toString()
            }

            binding.tvCommunityContent.text = feed.content
            binding.tvCommunityTitle.text = feed.travelLogTitle
            binding.tvCommunityNickname.text = feed.userNickname
            binding.btCommunityFollow.isChecked = feed.followState
            binding.tvCommunityLocation.text = feed.place
            binding.tvCommunityDatetime.text = feed.date
//            Glide.with(binding.root)
//                .load(feed.feedImage)
//                .into(binding.ivCommunityBg)
//            Glide.with(binding.root)
//                .load(feed.userProfile)
//                .into(binding.ivCommunityProfile)
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
        private const val DEFAULT_REACTION_COUNT = 99
    }
}
