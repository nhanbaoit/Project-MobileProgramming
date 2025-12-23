package com.example.doan.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.doan.model.InforBenhAn
import com.example.doan.R

class BenhAnSearchAdapter(
    private val activity: Activity,
    private val list: MutableList<InforBenhAn>
) : ArrayAdapter<InforBenhAn>(activity, R.layout.activity_item_pet, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(activity)
            .inflate(R.layout.activity_item_pet, parent, false)

        val iv = view.findViewById<ImageView>(R.id.ivHinhPet)
        val tv1 = view.findViewById<TextView>(R.id.tvMaBenhAn)
        val tv2 = view.findViewById<TextView>(R.id.tvTenThuCung)

        val item = list[position]

        iv.setImageResource(R.drawable.so_benh_an)
        tv1.text = "Ngày khám: ${item.ngay}"
        tv2.text = "Triệu chứng: ${item.trieuchung}"

        return view
    }
}
