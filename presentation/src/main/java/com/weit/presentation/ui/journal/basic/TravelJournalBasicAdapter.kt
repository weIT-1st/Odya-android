package com.weit.presentation.ui.journal.basic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemJournalDetailBasicModelBinding
import com.weit.presentation.model.journal.TravelJournalDetailInfo
import com.weit.presentation.ui.util.SpaceDecoration

class TravelJournalBasicAdapter: ListAdapter<TravelJournalDetailInfo, TravelJournalBasicAdapter.ViewHolder>(diffUtil) {
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
                // todo 여행일지 컨텐츠 수정
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
