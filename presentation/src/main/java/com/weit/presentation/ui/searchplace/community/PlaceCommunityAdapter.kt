package com.weit.presentation.ui.searchplace.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.community.CommunityMainContent
import com.weit.presentation.databinding.ItemFeedMyActivityImageBinding

class PlaceCommunityAdapter(
    private val navigateFeedDetail: (Long) -> Unit
): ListAdapter<CommunityMainContent, PlaceCommunityAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemFeedMyActivityImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ivFeedImage.setOnClickListener {
                navigateFeedDetail(getItem(absoluteAdapterPosition).communityId)
            }
        }
        fun bind(item : CommunityMainContent){
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

    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<CommunityMainContent>() {
            override fun areItemsTheSame(
                oldItem: CommunityMainContent,
                newItem: CommunityMainContent
            ): Boolean =
                oldItem.communityId == newItem.communityId


            override fun areContentsTheSame(
                oldItem: CommunityMainContent,
                newItem: CommunityMainContent
            ): Boolean =
                oldItem.toString() == newItem.toString()
        }
    }
}
