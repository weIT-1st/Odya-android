package com.weit.presentation.ui.feed.notification

import android.content.Context
import android.provider.Settings.System.getString
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.notification.NotificationInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemNotificationBinding
import com.weit.presentation.model.NotificationType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class NotificationListAdapter(
    private val action: (NotificationAction) -> Unit
) : ListAdapter<NotificationInfo, NotificationListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemNotificationBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                when (getItem(absoluteAdapterPosition).eventType) {
                    NotificationType.COMMUNITY_COMMENT.name -> {
                        getItem(absoluteAdapterPosition).contentId?.let { id ->
                            action(
                                NotificationAction.OnClickFeed(
                                    id,
                                    getItem(absoluteAdapterPosition).userName
                                )
                            )
                        }
                    }

                    NotificationType.COMMUNITY_LIKE.name -> {
                        getItem(absoluteAdapterPosition).contentId?.let { id ->
                            action(
                                NotificationAction.OnClickFeed(
                                    id,
                                    getItem(absoluteAdapterPosition).userName
                                )
                            )
                        }
                    }

                    NotificationType.FOLLOWING_COMMUNITY.name -> {
                        getItem(absoluteAdapterPosition).contentId?.let { id ->
                            action(
                                NotificationAction.OnClickFeed(
                                    id,
                                    getItem(absoluteAdapterPosition).userName
                                )
                            )
                        }
                    }

                    NotificationType.TRAVEL_JOURNAL_TAG.name -> {
                        getItem(absoluteAdapterPosition).contentId?.let { id ->
                            action(NotificationAction.OnClickTravelJournal(id))
                        }
                    }

                    NotificationType.FOLLOWING_TRAVEL_JOURNAL.name -> {
                        getItem(absoluteAdapterPosition).contentId?.let { id ->
                            action(NotificationAction.OnClickTravelJournal(id))
                        }
                    }

                    else -> {
                        action(
                            NotificationAction.OnClickOtherProfile(
                                getItem(
                                    absoluteAdapterPosition
                                ).userName
                            )
                        )
                    }
                }
            }
        }

        fun bind(item: NotificationInfo) {
            binding.noti = item
            val createdAt = getCreatedAt(binding.root.context,item.notifiedAt)
            val baseString = when (item.eventType) {
                NotificationType.COMMUNITY_COMMENT.name -> {
                    binding.root.context.getString(
                        R.string.notification_feed_comment,
                        item.userName,
                        item.commentContent,
                        createdAt
                    )
                }

                NotificationType.COMMUNITY_LIKE.name -> {
                    binding.root.context.getString(
                        R.string.notification_oyda,
                        item.userName,
                        createdAt
                    )
                }

                NotificationType.FOLLOWING_COMMUNITY.name -> {
                    binding.root.context.getString(
                        R.string.notification_feed_write,
                        item.userName,
                        createdAt
                    )
                }

                NotificationType.TRAVEL_JOURNAL_TAG.name -> {
                    binding.root.context.getString(
                        R.string.notification_travel_tag,
                        item.userName,
                        createdAt
                    )
                }

                NotificationType.FOLLOWING_TRAVEL_JOURNAL.name -> {
                    binding.root.context.getString(
                        R.string.notification_travel_write,
                        item.userName,
                        createdAt
                    )
                }

                else -> {
                    binding.root.context.getString(
                        R.string.notification_follow,
                        item.userName,
                        createdAt
                    )
                }
            }


            val text = HtmlCompat.fromHtml(
                baseString,
                HtmlCompat.FROM_HTML_MODE_LEGACY,
            )

            val spannableString = SpannableString(text)
            spannableString.apply {
                setSpan(
                    RelativeSizeSpan(0.8f),
                    text.indexOf("·"),
                    text.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.label_assistive
                        )
                    ), text.indexOf("·"), text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            binding.tvNotiContent.text = spannableString

            Glide.with(binding.root)
                .load(item.contentImage)
                .into(binding.ivNotiImage)
        }
    }

    private fun getCreatedAt(context: Context, date: String): String {

        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val dateTime = LocalDateTime.parse(date, dateFormat)

        val now = LocalDateTime.now()
        val diff = java.time.Duration.between(dateTime, now)
        val hours = diff.toHours()
        val days = diff.toDays()
        val createdAt =
            when {
                hours <= 3 -> context.getString(
                    R.string.feed_date_now
                )

                hours <= 24 -> context.getString(
                    R.string.feed_date_today
                )

                days <= 30 -> context.getString(
                    R.string.feed_date_days, days
                )

                else -> {
                    val formattedDate: String = if (dateTime.year == now.year) {
                        date.format(DateTimeFormatter.ofPattern("M월 d일", Locale.getDefault()))
                    } else {
                        date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.getDefault()))
                    }
                    formattedDate
                }
            }

        return createdAt
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
            override fun areItemsTheSame(
                oldItem: NotificationInfo,
                newItem: NotificationInfo
            ): Boolean =
                oldItem.notificationId == newItem.notificationId

            override fun areContentsTheSame(
                oldItem: NotificationInfo,
                newItem: NotificationInfo
            ): Boolean =
                oldItem == newItem
        }
    }
}
