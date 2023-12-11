package com.weit.presentation.ui.login.input.userinfo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.weit.presentation.R
import com.weit.presentation.databinding.DropdownLoginGenderBinding

class GenderOptionSpinnerAdapter(
    context: Context,
    @LayoutRes private val resId: Int,
    private val menuList: List<String>
): ArrayAdapter<String>(context, resId, menuList) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = DropdownLoginGenderBinding.inflate(LayoutInflater.from(parent.context))
        binding.tvLoginGenderSpinner.text = menuList[position]

        if (position == 0) {
            binding.tvLoginGenderSpinner.setTextColor(ContextCompat.getColor(context, R.color.label_inactive))
        } else {
            binding.tvLoginGenderSpinner.setTextColor(ContextCompat.getColor(context, R.color.label_normal))
        }

        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = DropdownLoginGenderBinding.inflate(LayoutInflater.from(parent.context))
        binding.tvLoginGenderSpinner.text = menuList[position]
        binding.tvLoginGenderSpinner.setTextColor(ContextCompat.getColor(context, R.color.label_normal))


        return binding.root
    }

    override fun getCount(): Int =
        menuList.size
}
