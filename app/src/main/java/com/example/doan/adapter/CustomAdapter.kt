package com.example.doan.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.doan.database.DBpet
import com.example.doan.model.InforPet
import com.example.doan.R
import com.example.doan.activity.SuaPetActivity

class CustomAdapter(val activity: Activity, val list: MutableList<InforPet>) :
    ArrayAdapter<InforPet>(activity, R.layout.list_item, list) {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val context = activity.layoutInflater
        val rowview = context.inflate(R.layout.list_item, parent, false)

        val image = rowview.findViewById<ImageView>(R.id.imgHinh)
        val Title = rowview.findViewById<TextView>(R.id.tvTitle)
        val chiTiet = rowview.findViewById<TextView>(R.id.tvInfor)
        val tvsua = rowview.findViewById<TextView>(R.id.tvSua)
        val tvXoa = rowview.findViewById<TextView>(R.id.tvXoa)
        Title.text = list[position].title
        chiTiet.text =
            "${list[position].breed} | ${list[position].weight} kg | ${list[position].chitiet}"
        image.setImageResource(list[position].image)

        tvsua.setOnClickListener {
            val intent = Intent(activity, SuaPetActivity::class.java)
            intent.putExtra(
                "Pet_ID",
                list[position].id
            )
            //chuyển dữ liệu id_pet từ mainActivity sang SuaPetActivity
            activity.startActivity(intent)
        }
        tvXoa.setOnClickListener {
            AlertDialog.Builder(activity)
                .setTitle("Xóa")
                .setMessage("Bạn có muốn xóa pet ${list[position].title}?")
                .setPositiveButton("OK") { dialog, which ->
                    val db = DBpet(activity)
                    db.XoaPet(list[position].id)
                    list.removeAt(position)
                    notifyDataSetChanged()
                }.setNegativeButton("Huy", null)
                .show()
        }
        return rowview
    }
}