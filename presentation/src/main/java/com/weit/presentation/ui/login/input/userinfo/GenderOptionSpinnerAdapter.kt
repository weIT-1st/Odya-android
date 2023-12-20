package com.weit.presentation.ui.login.input.userinfo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.weit.domain.model.GenderType
import com.weit.presentation.R
import com.weit.presentation.databinding.DropdownLoginGenderBinding

class GenderOptionSpinnerAdapter(
    context: Context,
    @LayoutRes private val resId: Int,
    private val menuList: List<String>
): ArrayAdapter<String>(context, resId, menuList) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val gender = positionToGenderType(position)

        val binding = DropdownLoginGenderBinding.inflate(LayoutInflater.from(parent.context))
        binding.tvLoginGenderSpinner.text = genderTypeToString(gender)

        if (gender == GenderType.IDLE) {
            binding.tvLoginGenderSpinner.setTextColor(ContextCompat.getColor(context, R.color.label_inactive))
        } else {
            binding.tvLoginGenderSpinner.setTextColor(ContextCompat.getColor(context, R.color.label_normal))
        }

        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val gender = positionToGenderType(position)

        val binding = DropdownLoginGenderBinding.inflate(LayoutInflater.from(parent.context))
        binding.tvLoginGenderSpinner.text = genderTypeToString(gender)
        binding.tvLoginGenderSpinner.setTextColor(ContextCompat.getColor(context, R.color.label_normal))


        return binding.root
    }

    override fun getCount(): Int =
        menuList.size

    private fun positionToGenderType(position: Int): GenderType =
        when(position){
            1 -> GenderType.MALE
            2 -> GenderType.FEMALE
            else -> GenderType.IDLE
        }

    private fun genderTypeToString(genderType: GenderType): String =
        when (genderType){
            GenderType.MALE -> menuList[1]
            GenderType.FEMALE -> menuList[2]
            else -> menuList[0]
        }
}
