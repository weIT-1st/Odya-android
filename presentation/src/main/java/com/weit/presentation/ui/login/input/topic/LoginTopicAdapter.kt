package com.weit.presentation.ui.login.input.topic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.databinding.ItemLoginChoiceTopicBinding
import com.weit.presentation.databinding.ItemLoginUnchoiceTopicBinding
import com.weit.presentation.model.topic.TopicChoiceInfo

class LoginTopicAdapter(
    private val updateItem: (Long) -> Unit
) : ListAdapter<TopicChoiceInfo, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CHOICE_TYPE -> ChoiceTopicViewHolder(ItemLoginChoiceTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> UnchoiceTopicViewHolder(ItemLoginUnchoiceTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ChoiceTopicViewHolder -> { holder.bind(item) }
            is UnchoiceTopicViewHolder -> { holder.bind(item) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isChoice) {
            CHOICE_TYPE
        } else {
            UNCHOICE_TYPE
        }
    }

    inner class UnchoiceTopicViewHolder(
        private val binding: ItemLoginUnchoiceTopicBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TopicChoiceInfo) {
            binding.tvItemChoiceTopic.text = item.topicWord
            binding.root.setOnClickListener {
                updateItem(item.topicId)
            }
        }
    }

    inner class ChoiceTopicViewHolder(
        private val binding: ItemLoginChoiceTopicBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TopicChoiceInfo) {
            binding.tvItemUnchoiceTopic.text = item.topicWord
            binding.root.setOnClickListener {
                updateItem(item.topicId)
            }
        }
    }

    companion object {
        private const val CHOICE_TYPE = 1
        private const val UNCHOICE_TYPE = 2

        private val diffUtil = object : DiffUtil.ItemCallback<TopicChoiceInfo>() {
            override fun areItemsTheSame(oldItem: TopicChoiceInfo, newItem: TopicChoiceInfo): Boolean =
                oldItem.topicId == newItem.topicId

            override fun areContentsTheSame(oldItem: TopicChoiceInfo, newItem: TopicChoiceInfo): Boolean =
                oldItem.toString() == newItem.toString()
        }
    }
}
