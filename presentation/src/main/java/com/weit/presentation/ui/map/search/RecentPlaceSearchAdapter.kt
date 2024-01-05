package com.weit.presentation.ui.map.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.databinding.ItemPlaceRecentSearchBinding

class RecentPlaceSearchAdapter(
    private val deleteItem: (MainSearchTopSheetViewModel.RecentSearchWord) -> Unit,
    private val touchItem: (String) -> Unit
): ListAdapter<MainSearchTopSheetViewModel.RecentSearchWord, RecentPlaceSearchAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlaceRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: ItemPlaceRecentSearchBinding
    ): RecyclerView.ViewHolder(binding.root){
        init {
            binding.tvItemPlaceRecentSearch.setOnClickListener {
                touchItem(getItem(absoluteAdapterPosition).recentWord)
            }
        }
        fun bind(item: MainSearchTopSheetViewModel.RecentSearchWord){
            binding.btnItemPlaceRecentSearchDelete.setOnClickListener { deleteItem(item) }
            binding.tvItemPlaceRecentSearch.text = item.recentWord
        }
    }

    companion object{
        private val DiffCallback: DiffUtil.ItemCallback<MainSearchTopSheetViewModel.RecentSearchWord> =
            object : DiffUtil.ItemCallback<MainSearchTopSheetViewModel.RecentSearchWord>(){
                override fun areItemsTheSame(oldItem: MainSearchTopSheetViewModel.RecentSearchWord, newItem: MainSearchTopSheetViewModel.RecentSearchWord): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: MainSearchTopSheetViewModel.RecentSearchWord, newItem: MainSearchTopSheetViewModel.RecentSearchWord): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
