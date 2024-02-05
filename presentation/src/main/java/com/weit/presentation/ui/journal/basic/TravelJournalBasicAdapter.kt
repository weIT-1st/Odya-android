package com.weit.presentation.ui.journal.basic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemJournalDetailBasicModelBinding
import com.weit.presentation.model.journal.TravelJournalDetailInfo
import com.weit.presentation.ui.util.SpaceDecoration

class TravelJournalBasicAdapter(
    private val updateContent: (contentId : Long) -> Unit,
    private val deleteContent: (contentId : Long) -> Unit
): ListAdapter<TravelJournalDetailInfo, TravelJournalBasicAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemJournalDetailBasicModelBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
    inner class ViewHolder(
        private val binding: ItemJournalDetailBasicModelBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val imageAdapter = TravelJournalBasicImageAdapter()
        init {
            binding.rvItemJournalDetailBasicContentImages.apply {
                adapter = imageAdapter
                addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_memory_all_space))
            }

            binding.btnItemJournalDetailDown.setOnCheckedChangeListener { _, isChecked ->
                binding.lyItemJournalDetailBasic.isGone = isChecked
            }

            binding.btnItemJournalDetailKebab.setOnClickListener {
                showPopUpMenu( getItem(absoluteAdapterPosition).travelJournalContentId ,it)
            }
        }

        fun bind(item: TravelJournalDetailInfo) {
            binding.tvItemJournalDetailBasicDay.text = binding.root.context.getString(R.string.journal_detail_day, layoutPosition + 1)
            binding.tvItemJournalDetailBasicDate.text = item.travelDate
            binding.tvItemJournalDetailBasicContent.text = item.content
            binding.tvItemJournalDetailBasicPlace.text = item.placeName
            binding.tvItemJournalDetailBasicAddress.text = item.placeAddress

            imageAdapter.submitList(item.travelJournalContentImages)
        }
    }

    private fun showPopUpMenu(contentId: Long, it : View) {
        val popupMenu = PopupMenu(it.context, it)
        popupMenu.menuInflater.inflate(R.menu.menu_travel_journal_content, popupMenu.menu)

        val updateContentItem = popupMenu.menu.findItem(R.id.item_content_update)
        val deleteContentItem = popupMenu.menu.findItem(R.id.item_content_delete)

        updateContentItem.title = it.context.getString(R.string.update_travel_log)
        deleteContentItem.title = it.context.getString(R.string.post_travel_log_daily_delete)

        if (updateContentItem.itemId == R.id.item_content_update) {
            val updateContentView = updateContentItem.actionView as? TextView
            updateContentView?.setTextColor(ContextCompat.getColor(it.context, R.color.label_normal))
        }
        if (deleteContentItem.itemId == R.id.item_content_delete) {
            val deleteContentView = deleteContentItem.actionView as? TextView
            deleteContentView?.setTextColor(ContextCompat.getColor(it.context, R.color.warning))
        }

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_content_update -> {
                    updateContent(contentId)
                }

                R.id.item_content_delete -> {
                    deleteContent(contentId)
                }
            }
            false
        }
        popupMenu.show()
    }

    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<TravelJournalDetailInfo>() {
            override fun areItemsTheSame(
                oldItem: TravelJournalDetailInfo,
                newItem: TravelJournalDetailInfo
            ): Boolean =
                oldItem.travelJournalContentId == newItem.travelJournalContentId

            override fun areContentsTheSame(
                oldItem: TravelJournalDetailInfo,
                newItem: TravelJournalDetailInfo
            ): Boolean =
                oldItem == newItem
        }
    }
}
