package com.weit.presentation.ui.journal.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.journal.TravelJournalContentsInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemJournalDetailAlbumModelBinding
import com.weit.presentation.databinding.ItemJournalDetailBasicModelBinding
import com.weit.presentation.ui.util.SpaceDecoration

class TravelJournalAlbumAdapter: ListAdapter<TravelJournalContentsInfo, TravelJournalAlbumAdapter.ViewHolder>(diffUtil) {

    private val travelJournalAlbumImageAdapter = TravelJournalAlbumImageAdapter()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemJournalDetailAlbumModelBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class ViewHolder(
        private val binding: ItemJournalDetailAlbumModelBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnItemJournalDetailDown.setOnClickListener {
                // todo 여행일지 컨텐츠 감추기
            }

            binding.btnItemJournalDetailKebab.setOnClickListener {
                // todo 여행일지 컨텐츠 수정
            }
        }

        fun bind(item: TravelJournalContentsInfo) {
            binding.tvItemJournalDetailAlbumDay.text = binding.root.context.getString(R.string.journal_detail_day, layoutPosition + 1)
            binding.tvItemJournalDetailAlbumDate.text = item.travelDate
            binding.tvItemJournalDetailAlbumContent.text = item.content
            binding.tvItemJournalDetailAlbumPlace.text = item.placeDetail.name
            binding.tvItemJournalDetailAlbumAddress.text =
                item.placeDetail.address?.split(" ")?.slice(1..2)?.joinToString(" ") ?: ""

            binding.rvItemJournalDetailAlbumContentImages.apply {
                adapter = travelJournalAlbumImageAdapter
                addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_memory_album_space, rightDP = R.dimen.item_memory_album_space))
            }
            travelJournalAlbumImageAdapter.submitList(item.travelJournalContentImages)
        }
    }

    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<TravelJournalContentsInfo>() {
            override fun areItemsTheSame(
                oldItem: TravelJournalContentsInfo,
                newItem: TravelJournalContentsInfo
            ): Boolean =
                oldItem.travelJournalContentId == newItem.travelJournalContentId

            override fun areContentsTheSame(
                oldItem: TravelJournalContentsInfo,
                newItem: TravelJournalContentsInfo
            ): Boolean =
                oldItem == newItem
        }
    }
}
