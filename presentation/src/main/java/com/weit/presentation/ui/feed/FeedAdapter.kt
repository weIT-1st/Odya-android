package com.weit.presentation.ui.feed


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.FeedItem
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemCommunityBinding
import com.weit.presentation.databinding.ItemMayknowFriendBinding
import com.weit.presentation.databinding.ItemPopularSpotBinding

class FeedAdapter(
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items : List<FeedItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_popular_spot -> PopularSpotViewHolder(ItemPopularSpotBinding.inflate(inflater, parent, false))
            R.layout.item_mayknow_friend -> MayKnowFriendViewHolder(ItemMayknowFriendBinding.inflate(inflater, parent, false))
            R.layout.item_community -> CommunityViewHolder(ItemCommunityBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is PopularSpotViewHolder -> holder.bind(item as FeedItem.PopularSpotItem)
            is MayKnowFriendViewHolder -> holder.bind(item as FeedItem.MayknowFriendItem)
            is CommunityViewHolder -> holder.bind(item as FeedItem.CommunityItem)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int) = when {
        position == 2 -> R.layout.item_popular_spot
        position == 5 -> R.layout.item_mayknow_friend
        items[position] is FeedItem.PopularSpotItem -> R.layout.item_popular_spot
        items[position] is FeedItem.MayknowFriendItem -> R.layout.item_mayknow_friend
        items[position] is FeedItem.CommunityItem -> R.layout.item_community
        else -> throw IllegalArgumentException("Unknown view type")
    }


    class PopularSpotViewHolder(
        private val binding: ItemPopularSpotBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(popularSpot: FeedItem.PopularSpotItem) {
            val data = popularSpot.popularSpotList
            val popularSpotRecyclerView = binding.rvPopularSpotSummary
            val popularSpotAdapter = PopularSpotAdapter()
            popularSpotAdapter.submitList(data)
            popularSpotRecyclerView.adapter = popularSpotAdapter
        }
    }

    class MayKnowFriendViewHolder(
        private val binding: ItemMayknowFriendBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mayKnowFriend: FeedItem.MayknowFriendItem) {
            val data = mayKnowFriend.mayKnowFriendList
            val mayKnowFriendRecyclerView = binding.rvMayknowFriendSummary
            val mayKnowFriendAdapter = MayKnowFriendAdapter()
            mayKnowFriendAdapter.submitList(data)
            mayKnowFriendRecyclerView.adapter = mayKnowFriendAdapter
        }
    }

    class CommunityViewHolder(
        private val binding: ItemCommunityBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(community: FeedItem.CommunityItem) {
            binding.tvCommunityTitle.text = community.title
            binding.tvCommunityNickname.text = community.nickname
        }
    }

}