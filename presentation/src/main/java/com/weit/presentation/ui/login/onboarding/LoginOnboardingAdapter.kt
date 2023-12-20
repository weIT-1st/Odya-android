package com.weit.presentation.ui.login.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.presentation.databinding.ItemLoginOnboardingBinding
import com.weit.presentation.model.LoginOnboardingModel

class LoginOnboardingAdapter(
): ListAdapter<LoginOnboardingModel, LoginOnboardingAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemLoginOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
    inner class ViewHolder(
        private val binding: ItemLoginOnboardingBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: LoginOnboardingModel){
            binding.tvLoginOnboardingMain.text = binding.root.context.getText(item.main)
            binding.tvLoginOnboardingDetail.text = binding.root.context.getText(item.detail)
            Glide.with(binding.root.context)
                .load(item.image)
                .into(binding.ivLoginOnboarding)
        }
    }

    companion object{
        private val DiffCallback: DiffUtil.ItemCallback<LoginOnboardingModel> =
            object : DiffUtil.ItemCallback<LoginOnboardingModel>(){
                override fun areItemsTheSame(
                    oldItem: LoginOnboardingModel,
                    newItem: LoginOnboardingModel
                ): Boolean =
                    oldItem.main == newItem.main

                override fun areContentsTheSame(
                    oldItem: LoginOnboardingModel,
                    newItem: LoginOnboardingModel
                ): Boolean =
                    oldItem == newItem
            }
    }
}
