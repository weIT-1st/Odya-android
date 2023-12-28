package com.weit.presentation.ui.journal.basic

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.weit.presentation.R

class ListViewAdapter(
    private val context: Context,
    private val images : List<String>
) : BaseAdapter() {
    override fun getCount(): Int =
        images.size

    override fun getItem(position: Int): Any =
        images[position]

    override fun getItemId(position: Int): Long =
        position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View =
            layoutInflater.inflate(R.layout.item_journal_detail_basic_image, parent, false)

        val item = images[position]
        val image = view.findViewById<ImageView>(R.id.iv_item_journal_detail_basic_image)

        Glide.with(context)
            .load(item)
            .into(image)

        return view
    }
}
