package com.weit.presentation.ui.map.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.weit.presentation.databinding.ItemPlaceRecentSearchBinding

class RecentPlaceSearchAdapter(
    private val deleteItem: (String) -> Unit,
    private val touchItem: (String) -> Unit
): ListAdapter<String, RecentPlaceSearchAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlaceRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recentPlaceSearch = getItem(position)
        holder.bind(recentPlaceSearch)
    }

    inner class ViewHolder(
        private val binding: ItemPlaceRecentSearchBinding
    ): RecyclerView.ViewHolder(binding.root){
        init {
            binding.tvItemPlaceRecentSearch.setOnClickListener {
                touchItem(getItem(absoluteAdapterPosition))
            }
        }
        fun bind(recentPlaceSearch: String){
            binding.btnItemPlaceRecentSearchDelete.setOnClickListener { deleteItem(recentPlaceSearch) }
            binding.tvItemPlaceRecentSearch.text = recentPlaceSearch
        }
    }

    companion object{
        private val DiffCallback: DiffUtil.ItemCallback<String> =
            object : DiffUtil.ItemCallback<String>(){
                override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
