package com.weit.presentation.ui.feed.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.user.SearchUserContent
import com.weit.presentation.databinding.ItemFeedRecentSearchBinding
import com.weit.presentation.databinding.ItemFeedSearchResultBinding

class SearchUserResultAdapter(
    private val selectUser: (SearchUserContent) -> Unit
): ListAdapter<SearchUserContent, SearchUserResultAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFeedSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemFeedSearchResultBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(recentUser: SearchUserContent){
            binding.lyFeedUserSearch.setOnClickListener { selectUser(recentUser) }
            binding.user = recentUser
        }
    }

    companion object{
        private val DiffCallback: DiffUtil.ItemCallback<SearchUserContent> =
            object : DiffUtil.ItemCallback<SearchUserContent>(){
                override fun areItemsTheSame(oldItem: SearchUserContent, newItem: SearchUserContent): Boolean {
                    return oldItem.userId == newItem.userId
                }

                override fun areContentsTheSame(oldItem: SearchUserContent, newItem: SearchUserContent): Boolean {
                    return oldItem.toString() == newItem.toString()
                }
            }
    }
}
