package com.example.doan.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.doan.model.InforPet
import com.example.doan.R

class PetSearchAdapter(
    private val activity: Activity,
    private val list: MutableList<InforPet>
) : ArrayAdapter<InforPet>(activity, R.layout.activity_item_pet, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(activity)
            .inflate(R.layout.activity_item_pet, parent, false)

        val iv = view.findViewById<ImageView>(R.id.ivHinhPet)
        val tv1 = view.findViewById<TextView>(R.id.tvMaBenhAn)
        val tv2 = view.findViewById<TextView>(R.id.tvTenThuCung)

        val item = list[position]

        iv.setImageResource(item.image)
        tv1.text = item.title
        tv2.text = "${item.breed} | ${item.weight} kg"

        return view
    }
}
