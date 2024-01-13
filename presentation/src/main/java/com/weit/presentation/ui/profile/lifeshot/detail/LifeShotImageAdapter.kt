package com.weit.presentation.ui.profile.lifeshot.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.presentation.databinding.ItemFeedPostImageBinding

class LifeShotImageAdapter(
) : ListAdapter<UserImageResponseInfo, LifeShotImageAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemFeedPostImageBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserImageResponseInfo) {
            Glide.with(binding.root)
                .load(item.imageUrl)
                .into(binding.ivFeedPost)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFeedPostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<UserImageResponseInfo>() {
            override fun areItemsTheSame(oldItem: UserImageResponseInfo, newItem: UserImageResponseInfo): Boolean =
                oldItem.imageId == newItem.imageId

            override fun areContentsTheSame(oldItem: UserImageResponseInfo, newItem: UserImageResponseInfo): Boolean =
                oldItem == newItem
        }
    }
}
