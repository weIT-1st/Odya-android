package com.weit.presentation.ui

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.weit.domain.model.user.UserProfile
import com.weit.presentation.R
import com.weit.presentation.ui.util.Constants.DEFAULT_REACTION_COUNT
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@BindingAdapter("image_uri")
fun bindImageUri(view: ImageView, uri: String?) {
    Glide.with(view.context)
        .load(uri)
        .placeholder(R.color.system_inactive)
        .into(view)
}

@BindingAdapter("profile_background")
fun bindProfileBackground(view: ImageView, profile: UserProfile?) {
    profile?.color?.let { color ->
        view.setBackgroundColor(Color.rgb(color.red, color.green, color.blue))
    }
}

@BindingAdapter("text_reaction_count")
fun bindReactionCount(textView: TextView, count: Int?) {
    count?.let { count ->
        textView.text =
            if (count > DEFAULT_REACTION_COUNT) {
                textView.resources.getString(
                    R.string.feed_reaction_over_count,
                    DEFAULT_REACTION_COUNT,
                )
            } else {
                count.toString()
            }
    }
}

@BindingAdapter("text_created_date")
fun bindCreatedDate(textView: TextView, date: LocalDateTime?) {
    if(date == null){
        textView.text = ""
    }
    date?.let { date ->
        val now = LocalDateTime.now()
        val diff = java.time.Duration.between(date, now)
        val hours = diff.toHours()
        val days = diff.toDays()

        when {
            hours < 3 -> textView.text = "방금전"
            hours < 24 -> textView.text = "오늘"
            days < 30 -> textView.text = "${days}일전"
            else -> textView.text =
                date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.getDefault()))
        }
    }
}
