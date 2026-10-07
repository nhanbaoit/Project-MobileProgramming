package com.example.doan.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.doan.repository.FirebaseBenhAnRepository
import com.example.doan.model.InforBenhAn
import com.example.doan.R
import com.example.doan.activity.SuaBenhAnActivity

class CustomAdapterBenhAn(val activity: Activity, val list: MutableList<InforBenhAn>) :
    ArrayAdapter<InforBenhAn>(activity, R.layout.list_item_benhan, list) {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val context = activity.layoutInflater
        val rowview = context.inflate(R.layout.list_item_benhan, parent, false)

        val ngay = rowview.findViewById<TextView>(R.id.tvNgay)
        val TrieuChung = rowview.findViewById<TextView>(R.id.tvTrieuChung)
        val ChuanDoan = rowview.findViewById<TextView>(R.id.tvChanDoan)
        val thuoc = rowview.findViewById<TextView>(R.id.tvThuoc)
        val xoaBenh = rowview.findViewById<TextView>(R.id.tvXoa)
        val suaBenh = rowview.findViewById<TextView>(R.id.tvSua)

        ngay.text = "Ngày: " + list[position].ngay
        TrieuChung.text = "Triệu chứng: " + list[position].trieuchung
        ChuanDoan.text = "Chuẩn đoán: " + list[position].chuanDoan
        thuoc.text = "Thuốc điều trị: " + list[position].thuoc

        suaBenh.setOnClickListener {
            val intent = Intent(activity, SuaBenhAnActivity::class.java)
            intent.putExtra("Ba_ID", list[position].id)
            activity.startActivity(intent)
        }
        xoaBenh.setOnClickListener {
            AlertDialog.Builder(activity)
                .setTitle("Xóa")
                .setMessage("Bạn có muốn xóa bản ghi bệnh này ?")
                .setPositiveButton("OK") { dialog, which ->
                    val repo = FirebaseBenhAnRepository()
                    repo.deleteBenhAn(list[position].id, {
                        Toast.makeText(activity, "Đã xóa", Toast.LENGTH_SHORT).show()
                        // Xóa xong cần update lại list hoặc gọi từ Activity. Ta tạm xoá trên adapter trước.
                        list.removeAt(position)
                        notifyDataSetChanged()
                    }, {
                        Toast.makeText(activity, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                    })
                }.setNegativeButton("Hủy", null)
                .show()
        }
        return rowview
    }
}