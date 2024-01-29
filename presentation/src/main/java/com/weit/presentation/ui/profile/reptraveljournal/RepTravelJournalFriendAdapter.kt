package com.weit.presentation.ui.profile.reptraveljournal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.weit.domain.model.reptraveljournal.RepTravelJournalListInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemProfileRepTravelJournalBinding
import com.weit.presentation.databinding.ItemRepRoundProfileBinding
import com.weit.presentation.databinding.ItemRoundProfileBigBinding

class RepTravelJournalFriendAdapter(
): ListAdapter<String, RepTravelJournalFriendAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRepRoundProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemRepRoundProfileBinding
    ): RecyclerView.ViewHolder(binding.root){

        fun bind(item: String){
            Logger.t("MainTest").i("$item")
            Glide.with(binding.root)
                .load(item)
                .into(binding.ivProfile)
        }
    }
    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<String>(){
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean =
                oldItem == newItem
        }
    }
}
