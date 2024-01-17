package com.weit.presentation.ui.feed.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.user.SearchUserContent
import com.weit.domain.model.user.search.UserSearchInfo
import com.weit.presentation.databinding.ItemFeedRecentSearchBinding

class RecentUserSearchAdapter(
    private val deleteItem: (UserSearchInfo) -> Unit,
    private val selectUser: (UserSearchInfo) -> Unit
): ListAdapter<UserSearchInfo, RecentUserSearchAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFeedRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemFeedRecentSearchBinding
    ): RecyclerView.ViewHolder(binding.root){

        init{
            binding.tvFeedUserSearchName.setOnClickListener {
                selectUser(getItem(absoluteAdapterPosition))
            }
        }
        fun bind(recentUser: UserSearchInfo){
            binding.btnFeedUserSearchDelete.setOnClickListener { deleteItem(recentUser) }
            binding.user = recentUser
        }
    }

    companion object{
        private val DiffCallback: DiffUtil.ItemCallback<UserSearchInfo> =
            object : DiffUtil.ItemCallback<UserSearchInfo>(){
                override fun areItemsTheSame(oldItem: UserSearchInfo, newItem: UserSearchInfo): Boolean {
                    return oldItem.userId == newItem.userId
                }

                override fun areContentsTheSame(oldItem: UserSearchInfo, newItem: UserSearchInfo): Boolean {
                    return oldItem.toString() == newItem.toString()
                }
            }
    }
}
