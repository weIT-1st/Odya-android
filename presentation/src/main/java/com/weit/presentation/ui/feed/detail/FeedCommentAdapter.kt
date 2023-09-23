package com.weit.presentation.ui.feed.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.community.comment.CommunityCommentContent
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemFeedCommentBinding

class FeedCommentAdapter(
    private val updateItem: (Int) -> Unit,
    private val deleteItem: (Int) -> Unit,
) : ListAdapter<CommunityCommentContent, FeedCommentAdapter.FeedCommentViewHolder>(DiffCallback) {

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

        fun bind(feedComment: CommunityCommentContent) {
            binding.comment = feedComment
            if(feedComment.isWriter){
                binding.btnItemFeedMenu.visibility = View.VISIBLE
                binding.btnItemFeedMenu.setOnClickListener {
                    showPopUpMenu(absoluteAdapterPosition,it)
                }
            }else{
                binding.btnItemFeedMenu.visibility = View.GONE
            }
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
        private val DiffCallback: DiffUtil.ItemCallback<CommunityCommentContent> =
            object : DiffUtil.ItemCallback<CommunityCommentContent>() {
                override fun areItemsTheSame(
                    oldItem: CommunityCommentContent,
                    newItem: CommunityCommentContent,
                ): Boolean {
                    return oldItem.communityCommentId == newItem.communityCommentId
                }

                override fun areContentsTheSame(
                    oldItem: CommunityCommentContent,
                    newItem: CommunityCommentContent,
                ): Boolean {
                    return oldItem.toString() == newItem.toString()
                }
            }
    }
}
