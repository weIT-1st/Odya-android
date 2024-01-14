package com.weit.presentation.ui.profile.otherprofile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.presentation.databinding.ItemProfileLifeshotBinding

class OtherProfileLifeShotAdapter(
    ) : ListAdapter<UserImageResponseInfo, OtherProfileLifeShotAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemProfileLifeshotBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserImageResponseInfo) {
            Glide.with(binding.root)
                .load(item.imageUrl)
                .into(binding.ivLifeshot)
            binding.tvLocation.text = item.placeName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProfileLifeshotBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<UserImageResponseInfo>() {
            override fun areItemsTheSame(oldItem: UserImageResponseInfo, newItem: UserImageResponseInfo): Boolean =
                oldItem.toString() == newItem.toString()

            override fun areContentsTheSame(oldItem: UserImageResponseInfo, newItem: UserImageResponseInfo): Boolean =
                oldItem == newItem
        }
    }
}
