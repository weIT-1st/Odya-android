package com.weit.presentation.ui.feed.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.databinding.ItemFeedCommentBinding
import com.weit.presentation.model.FeedComment

class FeedCommentAdapter(
) : ListAdapter<FeedComment, FeedCommentAdapter.FeedCommentViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedCommentViewHolder {
        return FeedCommentViewHolder(
            ItemFeedCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(holder: FeedCommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FeedCommentViewHolder(
        private val binding: ItemFeedCommentBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(feedComment: FeedComment) {
            binding.comment = feedComment
        }
    }

    companion object {
        private val DiffCallback: DiffUtil.ItemCallback<FeedComment> =
            object : DiffUtil.ItemCallback<FeedComment>() {
                override fun areItemsTheSame(
                    oldItem: FeedComment,
                    newItem: FeedComment,
                ): Boolean {
                    return oldItem.commentId == newItem.commentId
                }

                override fun areContentsTheSame(
                    oldItem: FeedComment,
                    newItem: FeedComment,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
