package com.weit.presentation.ui.feed.myactivity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.community.CommunityMyActivityContent
import com.weit.presentation.databinding.ItemFeedMyActivityImageBinding
import com.weit.presentation.databinding.ItemFeedPostImageBinding

class FeedMyActivityImageAdapter(
    private val navigateFeedDetail: (Long) -> Unit,
) : ListAdapter<CommunityMyActivityContent, FeedMyActivityImageAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemFeedMyActivityImageBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        init{
            binding.ivFeedImage.setOnClickListener {
                navigateFeedDetail(getItem(absoluteAdapterPosition).communityId)
            }
        }
        fun bind(item: CommunityMyActivityContent) {
            Glide.with(binding.root)
                .load(item.communityMainImageUrl)
                .into(binding.ivFeedImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFeedMyActivityImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<CommunityMyActivityContent>() {
            override fun areItemsTheSame(
                oldItem: CommunityMyActivityContent,
                newItem: CommunityMyActivityContent
            ): Boolean =
                oldItem.communityId == newItem.communityId

            override fun areContentsTheSame(
                oldItem: CommunityMyActivityContent,
                newItem: CommunityMyActivityContent
            ): Boolean =
                oldItem.toString() == newItem.toString()
        }
    }
}
