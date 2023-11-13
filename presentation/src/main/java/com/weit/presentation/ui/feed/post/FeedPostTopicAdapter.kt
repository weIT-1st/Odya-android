package com.weit.presentation.ui.feed.post

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemTopicBinding
import com.weit.presentation.model.feed.FeedTopic
import com.weit.presentation.ui.util.Constants.TOPIC_CORNER_RADIUS

class FeedPostTopicAdapter(
    private var selectTopic : (Long) -> Unit,
) :
    androidx.recyclerview.widget.ListAdapter<FeedTopic, FeedPostTopicAdapter.FeedPostTopicViewHolder>(
        FeedTopicDiffCallback,
    ) {
    inner class FeedPostTopicViewHolder(
        private val binding: ItemTopicBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        val gradientDrawable = GradientDrawable()
        init{
            gradientDrawable.shape = GradientDrawable.RECTANGLE
            gradientDrawable.cornerRadii = floatArrayOf(TOPIC_CORNER_RADIUS, TOPIC_CORNER_RADIUS, TOPIC_CORNER_RADIUS, TOPIC_CORNER_RADIUS, TOPIC_CORNER_RADIUS, TOPIC_CORNER_RADIUS, TOPIC_CORNER_RADIUS, TOPIC_CORNER_RADIUS)
        }
        fun bind(topic: FeedTopic) {
            binding.tvTopic.text = topic.topicWord
            binding.tvTopic.setOnClickListener {
                selectTopic(topic.topicId)
            }

            binding.tvTopic.setBackgroundResource(R.drawable.corners_all_20)

            val context = binding.root.context
            val cornerRadius = 100f

            val textColor = if (topic.isChecked) {
                ContextCompat.getColor(context, R.color.background_normal)
            } else {
                ContextCompat.getColor(context, R.color.label_inactive)
            }

            val bgColor = if (topic.isChecked) {
                ContextCompat.getColor(context, R.color.primary)
            } else {
                ContextCompat.getColor(context, R.color.system_inactive)
            }

            gradientDrawable.setColor(bgColor)

            binding.tvTopic.setTextColor(textColor)
            binding.tvTopic.background = gradientDrawable
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedPostTopicViewHolder {
        return FeedPostTopicViewHolder(
            ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: FeedPostTopicViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val FeedTopicDiffCallback: DiffUtil.ItemCallback<FeedTopic> =
            object : DiffUtil.ItemCallback<FeedTopic>() {
                override fun areItemsTheSame(oldItem: FeedTopic, newItem: FeedTopic): Boolean {
                    return oldItem.topicId == newItem.topicId
                }

                override fun areContentsTheSame(oldItem: FeedTopic, newItem: FeedTopic): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
