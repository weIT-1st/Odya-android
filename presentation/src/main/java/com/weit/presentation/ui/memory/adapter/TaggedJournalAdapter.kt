package com.weit.presentation.ui.memory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemJournalMemoryTagJournalBinding

class TaggedJournalAdapter(
    private val showDetail: (Long) -> Unit,
    private val deleteItem : () -> Unit
): ListAdapter<TravelJournalListInfo, TaggedJournalAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemJournalMemoryTagJournalBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: ItemJournalMemoryTagJournalBinding
    ): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                showDetail( getItem( absoluteAdapterPosition ).travelJournalId )
            }

            binding.toggleItemJournalMemoryBookmark.setOnClickListener {
                // todo bookmark
            }
        }
        fun bind(item: TravelJournalListInfo){
            Glide.with(binding.root.context)
                .load(item.contentImageUrl)
                .into(binding.ivItemJournalMemoryLastJournal)

            Glide.with(binding.root.context)
                .load(item.writer.profile)
                .into(binding.includeItemJournalMemoryDetail.ivJournalMemoryDetailProfile)

            binding.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxTitle.text = item.travelJournalTitle
            binding.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxDate.text = item.travelStartDate
            binding.btnItemJournalMemoryMenu.setOnClickListener {
                showPopUpMenu(binding.btnItemJournalMemoryMenu)
            }
        }
    }

    private fun showPopUpMenu(it: View){
        val contextThemeWrapper = ContextThemeWrapper(it.context, R.style.MemoryPopupMenuStyle)

        PopupMenu(contextThemeWrapper, it).apply {
            menuInflater.inflate(R.menu.menu_memory, this.menu)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_memory_delete -> {
                        deleteItem
                    }
                }
                false
            }
        }.show()
    }

    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<TravelJournalListInfo>() {
            override fun areItemsTheSame(
                oldItem: TravelJournalListInfo,
                newItem: TravelJournalListInfo
            ): Boolean =
                oldItem.travelJournalId == newItem.travelJournalId

            override fun areContentsTheSame(
                oldItem: TravelJournalListInfo,
                newItem: TravelJournalListInfo
            ): Boolean =
                oldItem == newItem

        }
    }
}
