package com.weit.presentation.ui.journal.friends
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.weit.domain.model.journal.TravelJournalCompanionsInfo
//
//class TravelJournalFriendAdapter: ListAdapter<TravelJournalCompanionsInfo, TravelJournalFriendAdapter.ViewHolder>(
//    diffUtil
//) {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
//        = ViewHolder(ItemFriendsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = getItem(position)
//        holder.bind(item)
//    }
//
//    inner class ViewHolder(
//        private val binding: ItemFriendsBinding
//    ): RecyclerView.ViewHolder(binding.root){
//        fun bind(item: TravelJournalCompanionsInfo) {
//            binding.friends = item
//        }
//    }
//    companion object{
//        private val diffUtil = object : DiffUtil.ItemCallback<TravelJournalCompanionsInfo>() {
//            override fun areItemsTheSame(
//                oldItem: TravelJournalCompanionsInfo,
//                newItem: TravelJournalCompanionsInfo
//            ): Boolean =
//                oldItem.userId == newItem.userId
//
//            override fun areContentsTheSame(
//                oldItem: TravelJournalCompanionsInfo,
//                newItem: TravelJournalCompanionsInfo
//            ): Boolean =
//                oldItem == newItem
//        }
//    }
//
//}
