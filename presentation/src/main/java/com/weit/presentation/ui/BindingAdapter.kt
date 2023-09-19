package com.weit.presentation.ui

import android.graphics.Color
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.weit.domain.model.user.UserProfile
import com.weit.presentation.R

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

