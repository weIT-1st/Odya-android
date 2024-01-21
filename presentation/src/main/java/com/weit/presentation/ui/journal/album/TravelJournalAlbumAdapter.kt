package com.weit.presentation.ui.journal.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemJournalDetailAlbumModelBinding
import com.weit.presentation.model.journal.TravelJournalDetailInfo
import com.weit.presentation.ui.util.SpaceDecoration

class TravelJournalAlbumAdapter :
    ListAdapter<TravelJournalDetailInfo, TravelJournalAlbumAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemJournalDetailAlbumModelBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemJournalDetailAlbumModelBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val imageAdapter = TravelJournalAlbumImageAdapter()

        init {
            binding.rvItemJournalDetailAlbumContentImages.apply {
                adapter = imageAdapter
                addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_memory_album_space, rightDP = R.dimen.item_memory_album_space))
            }

            binding.btnItemJournalDetailDown.setOnCheckedChangeListener { _, isChecked ->
                binding.lyItemJournalDetailAlbum.isGone = isChecked
            }

            binding.btnItemJournalDetailKebab.setOnClickListener {
                // todo 여행일지 컨텐츠 수정
            }
        }

        fun bind(item: TravelJournalDetailInfo) {
            binding.tvItemJournalDetailAlbumDay.text = binding.root.context.getString(R.string.journal_detail_day, layoutPosition + 1)
            binding.tvItemJournalDetailAlbumDate.text = item.travelDate
            binding.tvItemJournalDetailAlbumContent.text = item.content
            binding.tvItemJournalDetailAlbumPlace.text = item.placeName
            binding.tvItemJournalDetailAlbumAddress.text = item.placeAddress

            imageAdapter.submitList(item.travelJournalContentImages)
        }
    }

    companion object {
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
