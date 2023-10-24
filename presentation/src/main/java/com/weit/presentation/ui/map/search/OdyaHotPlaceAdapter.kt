package com.weit.presentation.ui.map.search


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.doOnNextLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemOdyaHotPlaceRankBinding
import com.weit.presentation.model.HotPlaceRank

class OdyaHotPlaceAdapter(

): ListAdapter<HotPlaceRank, OdyaHotPlaceAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val halfWidth = ItemOdyaHotPlaceRankBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        parent.doOnNextLayout {
            val params = halfWidth.root.layoutParams
            params.width = parent.width / 2
            halfWidth.root.layoutParams = params
        }
        return ViewHolder(
            halfWidth
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
    class ViewHolder(
        private val binding: ItemOdyaHotPlaceRankBinding
    ):RecyclerView.ViewHolder(binding.root){
        fun bind(item : HotPlaceRank){
            val rankText = itemView.context.getString(R.string.place_main_hot_place_rank, item.rank)
            if (item.rank < 4){
                binding.tvItemHotPlaceRank.setTextColor(ContextCompat.getColor(itemView.context.applicationContext, R.color.primary))
            } else {
                binding.tvItemHotPlaceRank.setTextColor(ContextCompat.getColor(itemView.context.applicationContext, R.color.system_inactive))
            }
            binding.tvItemHotPlaceRank.text = rankText
            binding.tvItemHotPlaceRankTitle.text = item.place
        }
    }
    companion object{
        private val DiffCallback: DiffUtil.ItemCallback<HotPlaceRank> =
            object : DiffUtil.ItemCallback<HotPlaceRank>(){
                override fun areItemsTheSame(oldItem: HotPlaceRank, newItem: HotPlaceRank): Boolean {
                    return oldItem.rank == newItem.rank
                }

                override fun areContentsTheSame(oldItem: HotPlaceRank, newItem: HotPlaceRank): Boolean {
                    return oldItem == newItem
                }

            }
    }
}
