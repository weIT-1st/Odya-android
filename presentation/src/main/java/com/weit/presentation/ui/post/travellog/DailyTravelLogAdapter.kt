package com.weit.presentation.ui.post.travellog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemDailyTravelLogBinding
import com.weit.presentation.model.post.travellog.DailyTravelLog
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class DailyTravelLogAdapter(
    private val action: (DailyTravelLogAction) -> Unit,
) : ListAdapter<DailyTravelLog, DailyTravelLogAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemDailyTravelLogBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val pictureAdapter = DailyTravelLogPictureAdapter { position ->
            action(DailyTravelLogAction.OnDeletePicture(absoluteAdapterPosition, position))
            deleteSelectedPicture(position)
        }

        init {
            binding.etDailyTravelLogContents.addTextChangedListener {
                getItem(absoluteAdapterPosition).contents = it.toString()
            }
            binding.btnDailyTravelLogSelectPicture.setOnClickListener {
                action(DailyTravelLogAction.OnSelectPicture(absoluteAdapterPosition))
            }
            binding.btnDailyTravelLogDate.setOnClickListener {
                action(DailyTravelLogAction.OnPickDate(absoluteAdapterPosition))
            }
            binding.btnDailyTravelLogPlace.setOnClickListener {
                val item = getItem(absoluteAdapterPosition)
                action(
                    DailyTravelLogAction.OnSelectPlace(
                        position = absoluteAdapterPosition,
                        images = item.images,
                    ),
                )
            }
            binding.btnDailyTravelLogDelete.setOnClickListener {
                action(DailyTravelLogAction.OnDeleteDailyTravelLog(absoluteAdapterPosition))
            }
        }

        fun bind(item: DailyTravelLog) {
            binding.tvDailyTravelLogDays.text =
                binding.root.context.getString(R.string.post_travel_log_daily_day, absoluteAdapterPosition + 1)
            binding.btnDailyTravelLogSelectPicture.text =
                binding.root.context.getString(R.string.post_travel_log_daily_picture_count, item.images.size, MAX_PICTURES)
            binding.btnDailyTravelLogDate.text =
                item.date?.toDateString() ?: binding.root.context.getString(R.string.date_picker_daily_empty)
            val placeName = item.place?.name ?: binding.root.context.getString(R.string.post_travel_log_daily_place)
            binding.btnDailyTravelLogPlace.text = placeName
            binding.btnDailyTravelLogDelete.isVisible = absoluteAdapterPosition != 0
            binding.etDailyTravelLogContents.setText(item.contents)
            pictureAdapter.submitList(item.images)
            binding.rvDailyTravelLogPictures.adapter = pictureAdapter
        }

        private fun LocalDate.toDateString(): String {
            return binding.root.context.getString(
                R.string.date_picker_daily,
                year,
                monthValue,
                dayOfMonth,
                dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            )
        }

        private fun deleteSelectedPicture(imageIndex: Int) {
            val newImages = pictureAdapter.currentList.toMutableList().apply {
                removeAt(imageIndex)
            }
            pictureAdapter.submitList(newImages)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDailyTravelLogBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private const val MAX_PICTURES = 15
        private val diffUtil = object : DiffUtil.ItemCallback<DailyTravelLog>() {
            override fun areItemsTheSame(oldItem: DailyTravelLog, newItem: DailyTravelLog): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DailyTravelLog, newItem: DailyTravelLog): Boolean =
                oldItem == newItem
        }
    }
}
