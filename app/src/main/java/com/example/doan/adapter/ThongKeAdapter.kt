package com.example.doan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.doan.R
import com.example.doan.model.ThongKe

class ThongKeAdapter(val context: Context, val list: List<ThongKe>) : BaseAdapter() {

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Any = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // 1. Khởi tạo hoặc tái sử dụng View (item_thong_ke.xml)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_thong_ke, parent, false)

        // 2. Ánh xạ các thành phần giao diện từ CardView
        val tvLabel = view.findViewById<TextView>(R.id.tvLabel)
        val tvValue = view.findViewById<TextView>(R.id.tvValue)

        // 3. Lấy dữ liệu tại vị trí hiện tại
        val item = list[position]

        // 4. Đổ dữ liệu vào giao diện
        tvLabel.text = item.tieuDe
        tvValue.text = item.giaTri

        // --- TÙY CHỈNH THÊM (Mượt hơn) ---
        // Nếu giá trị là "0", ta có thể đổi màu sang đỏ để cảnh báo
        if (item.giaTri == "0" || item.giaTri.startsWith("0")) {
            tvValue.setTextColor(context.getColor(android.R.color.holo_red_dark))
        } else {
            // Ngược lại để màu xanh dương mặc định như bạn đã thiết kế
            tvValue.setTextColor(context.getColor(android.R.color.holo_blue_dark))
        }

        return view
    }
}