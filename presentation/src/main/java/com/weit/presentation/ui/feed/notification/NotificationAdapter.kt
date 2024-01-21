package com.weit.presentation.ui.feed.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.notification.NotificationInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemNotificationBinding
import com.weit.presentation.model.NotificationType

class NotificationAdapter(
) : ListAdapter<NotificationInfo, NotificationAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemNotificationBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationInfo) {
           binding.noti = item
            val baseString = when(item.eventType){
                NotificationType.COMMUNITY_COMMENT.name -> {
                    binding.root.context.getString(
                        R.string.notification_feed_comment,
                        item.userName,
                        item.commentContent,
                        item.notifiedAt
                    )
                }
                NotificationType.COMMUNITY_LIKE.name -> {
                    binding.root.context.getString(
                        R.string.notification_oyda,
                        item.userName,
                        item.notifiedAt
                    )
                }
                NotificationType.FOLLOWING_COMMUNITY.name -> {
                    binding.root.context.getString(
                        R.string.notification_feed_write,
                        item.userName,
                        item.notifiedAt
                    )
                }
                NotificationType.TRAVEL_JOURNAL_TAG.name -> {
                    binding.root.context.getString(
                        R.string.notification_travel_tag,
                        item.userName,
                        item.notifiedAt
                    )
                }
                NotificationType.FOLLOWING_TRAVEL_JOURNAL.name -> {
                    binding.root.context.getString(
                        R.string.notification_travel_write,
                        item.userName,
                        item.notifiedAt
                    )
                }
                else -> {
                    binding.root.context.getString(
                        R.string.notification_follow,
                        item.userName,
                        item.notifiedAt
                    )
                }
            }

            binding.tvNotiContent.text = HtmlCompat.fromHtml(
                baseString,
                HtmlCompat.FROM_HTML_MODE_COMPACT,
            )
            Glide.with(binding.root)
                .load(item.contentImage)
                .into(binding.ivNotiImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<NotificationInfo>() {
            override fun areItemsTheSame(oldItem: NotificationInfo, newItem: NotificationInfo): Boolean =
                oldItem.notificationId == newItem.notificationId

            override fun areContentsTheSame(oldItem: NotificationInfo, newItem: NotificationInfo): Boolean =
                oldItem == newItem
        }
    }
}
