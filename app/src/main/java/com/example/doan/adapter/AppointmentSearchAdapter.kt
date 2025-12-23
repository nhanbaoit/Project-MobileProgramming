package com.example.doan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.doan.R
import com.example.dd1_doan_lan4.Appointment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppointmentSearchAdapter(
    context: Context,
    private val data: MutableList<Appointment>
) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)
    private val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun getCount(): Int = data.size
    override fun getItem(position: Int): Appointment = data[position]
    override fun getItemId(position: Int): Long = data[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_appointment_search, parent, false)
            holder = ViewHolder(
                tvTitle = view.findViewById(R.id.tvApptTitle),
                tvSub = view.findViewById(R.id.tvApptSub),
                tvStatus = view.findViewById(R.id.tvApptStatus)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val appt = data[position]

        holder.tvTitle.text = "🐾 ${appt.petName} | 🏥 ${appt.serviceType}"
        holder.tvSub.text = "⏰ ${fmt.format(Date(appt.dateTime))} • 👨‍⚕️ ${appt.doctorName}"

        if (appt.isStatus) {
            holder.tvStatus.text = "✅ Đã"
            holder.tvStatus.setBackgroundColor(0xFFE8F5E9.toInt())
        } else {
            holder.tvStatus.text = "⏳ Chưa"
            holder.tvStatus.setBackgroundColor(0xFFFFF3E0.toInt())
        }

        return view
    }

    private data class ViewHolder(
        val tvTitle: TextView,
        val tvSub: TextView,
        val tvStatus: TextView
    )
}
