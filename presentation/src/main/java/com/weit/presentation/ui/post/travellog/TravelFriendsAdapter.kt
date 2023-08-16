package com.weit.presentation.ui.post.travellog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.user.UserProfile
import com.weit.presentation.databinding.ItemRoundProfileBinding

class TravelFriendsAdapter : ListAdapter<UserProfile, TravelFriendsAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(
        private val binding: ItemRoundProfileBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserProfile) {
            binding.profile = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRoundProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<UserProfile>() {
            override fun areItemsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean =
                oldItem.toString() == oldItem.toString()

            override fun areContentsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean =
                oldItem.toString() == oldItem.toString()
        }
    }
}
