package com.weit.presentation.ui.feed.myactivity.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.community.CommunityMyActivityCommentContent
import com.weit.domain.model.community.CommunityMyActivityContent
import com.weit.presentation.databinding.ItemFeedMyActivityCommentBinding
import com.weit.presentation.databinding.ItemFeedMyActivityImageBinding
import com.weit.presentation.databinding.ItemFeedPostImageBinding

class FeedMyActivityCommentAdapter(
    private val navigateFeedDetail: (Long) -> Unit,
) : ListAdapter<CommunityMyActivityCommentContent, FeedMyActivityCommentAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemFeedMyActivityCommentBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        init{
            itemView.setOnClickListener {
                navigateFeedDetail(getItem(absoluteAdapterPosition).communityId)
            }
        }
        fun bind(item: CommunityMyActivityCommentContent) {
            binding.content = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFeedMyActivityCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<CommunityMyActivityCommentContent>() {
            override fun areItemsTheSame(
                oldItem: CommunityMyActivityCommentContent,
                newItem: CommunityMyActivityCommentContent
            ): Boolean =
                oldItem.communityId == newItem.communityId

            override fun areContentsTheSame(
                oldItem: CommunityMyActivityCommentContent,
                newItem: CommunityMyActivityCommentContent
            ): Boolean =
                oldItem.toString() == newItem.toString()
        }
    }
}
