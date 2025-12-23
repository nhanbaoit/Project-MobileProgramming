package com.example.doan.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.doan.model.PetRecord
import com.example.doan.R


class PetAdapter(context: Context, val res: Int, val ds: List<PetRecord>) :
    ArrayAdapter<PetRecord>(context, res, ds) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(res, null)


        val ivHinh = view.findViewById<ImageView>(R.id.ivHinhPet)
        val tvMa = view.findViewById<TextView>(R.id.tvMaBenhAn)
        val tvTen = view.findViewById<TextView>(R.id.tvTenThuCung)


        val item = ds[position]


        val resId = context.resources.getIdentifier(item.hinh, "drawable", context.packageName)
        ivHinh.setImageResource(if (resId != 0) resId else R.drawable.ic_launcher_background)


        tvMa.text = item.maBenhAn
        tvTen.text = item.tenThuCung


        return view
    }
}
