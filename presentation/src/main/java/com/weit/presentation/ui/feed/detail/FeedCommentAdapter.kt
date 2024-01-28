package com.weit.presentation.ui.feed.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.community.comment.CommentContent
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemFeedCommentBinding

class FeedCommentAdapter(
    private val updateItem: (Int) -> Unit,
    private val deleteItem: (Int) -> Unit,
) : ListAdapter<CommentContent, FeedCommentAdapter.FeedCommentViewHolder>(DiffCallback) {

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

        init{
            binding.btnItemFeedMenu.setOnClickListener {
                showPopUpMenu(absoluteAdapterPosition,it)
            }
        }
        fun bind(feedComment: CommentContent) {
            binding.comment = feedComment
            binding.btnItemFeedMenu.isVisible = feedComment.isWriter
        }
    }

    private fun showPopUpMenu(position:Int, it: View) {
        PopupMenu(it.context, it).apply {
           menuInflater.inflate(R.menu.menu_feed_comment, this.menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_update_comment -> {
                        updateItem(position)
                    }
                    R.id.item_delete_comment -> {
                        deleteItem(position)
                    }
                }
                false
            }
        }.show()
    }
    companion object {
        private val DiffCallback: DiffUtil.ItemCallback<CommentContent> =
            object : DiffUtil.ItemCallback<CommentContent>() {
                override fun areItemsTheSame(
                    oldItem: CommentContent,
                    newItem: CommentContent,
                ): Boolean {
                    return oldItem.communityCommentId == newItem.communityCommentId
                }

                override fun areContentsTheSame(
                    oldItem: CommentContent,
                    newItem: CommentContent,
                ): Boolean {
                    return oldItem.toString() == newItem.toString()
                }
            }
    }
}
