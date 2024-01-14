package com.weit.presentation.ui.profile.lifeshot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.presentation.databinding.ItemFeedPostImageBinding
import com.weit.presentation.databinding.ItemLifeshotImageBinding

class LifeShotImageAdapter(
    private val selectImage: (Long,String) -> Unit,
    ) : ListAdapter<UserImageResponseInfo, LifeShotImageAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemLifeshotImageBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ivLifeshotImage.setOnClickListener {
                val item = getItem(absoluteAdapterPosition)
                selectImage(item.imageId,item.imageUrl)
            }
        }
        fun bind(item: UserImageResponseInfo) {
            Glide.with(binding.root)
                .load(item.imageUrl)
                .into(binding.ivLifeshotImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLifeshotImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
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
