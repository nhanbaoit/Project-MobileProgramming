package com.example.doan.adapter

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.dd1_doan_lan4.Appointment
import com.example.doan.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppointmentListAdapter(
    private val context: Context,
    private var listData: List<Appointment>,
    private val onEditClick: (Appointment) -> Unit,
    private val onDeleteClick: (Appointment) -> Unit
) : BaseAdapter() {

    private val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
    private val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun getCount(): Int = listData.size
    override fun getItem(position: Int): Any = listData[position]
    override fun getItemId(position: Int): Long = listData[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_time_slot, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val item = listData[position]

        // 1. Hiển thị ngày giờ
        val dateObj = Date(item.dateTime)
        holder.tvDate.text = dateFormat.format(dateObj)
        holder.tvYear.text = yearFormat.format(dateObj)
        holder.tvTime.text = timeFormat.format(dateObj)

        holder.tvPetName.text = item.petName
        holder.tvService.text = "DV: ${item.serviceType}"
        holder.tvDoctor.text = item.doctorName

        // ============================================================
        // ✅ 3. LOGIC TRẠNG THÁI THÔNG MINH (3 TRƯỜNG HỢP)
        // ============================================================

        val currentTime = System.currentTimeMillis() // Lấy thời gian hiện tại

        if (item.isStatus) {
            // TRƯỜNG HỢP 1: Đã được tích chọn -> "Đã hoàn thành" (Xanh Lá)
            holder.tvStatus.text = "✅ Đã hoàn thành"
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
            holder.layoutDateBox.setBackgroundColor(Color.parseColor("#4CAF50"))
        } else if (item.dateTime < currentTime) {
            // TRƯỜNG HỢP 2: Chưa tích chọn NHƯNG đã qua giờ hẹn -> "Chưa hoàn thành" (Đỏ/Xám)
            holder.tvStatus.text = "⚠️ Chưa hoàn thành"
            holder.tvStatus.setTextColor(Color.parseColor("#F44336")) // Chữ đỏ
            holder.layoutDateBox.setBackgroundColor(Color.parseColor("#9E9E9E")) // Hộp xám (để báo hiệu đã qua)
        } else {
            // TRƯỜNG HỢP 3: Chưa tích chọn VÀ chưa đến giờ -> "Đang chờ" (Cam/Tím)
            holder.tvStatus.text = "⏳ Đang chờ"
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800")) // Chữ cam
            holder.layoutDateBox.setBackgroundColor(Color.parseColor("#673AB7")) // Hộp tím
        }
        // ============================================================


        holder.btnEdit.setOnClickListener { onEditClick(item) }
        holder.btnDelete.setOnClickListener {
            val sharedPreferences = context.getSharedPreferences("UserRole", Context.MODE_PRIVATE)
            val role = sharedPreferences.getString("ROLE", "USER")
            if (role == "USER") {
                if (item.isStatus) {
                    android.widget.Toast.makeText(
                        context,
                        "Không thể xóa cuộc hẹn này",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                } else {
                    onDeleteClick(item)
                }
            } else {
                onDeleteClick(item)
            }
        }

        return view
    }

    private class ViewHolder(view: View) {
        val layoutDateBox: LinearLayout = view.findViewById(R.id.layoutDateBox)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)

        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvYear: TextView = view.findViewById(R.id.tvYear)
        val tvTime: TextView = view.findViewById(R.id.tvTime)

        val tvPetName: TextView = view.findViewById(R.id.tvPetName)
        val tvService: TextView = view.findViewById(R.id.tvService)
        val tvDoctor: TextView = view.findViewById(R.id.tvDoctor)

        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }
}