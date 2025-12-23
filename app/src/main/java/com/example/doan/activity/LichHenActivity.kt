package com.example.doan.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dd1_doan_lan4.Appointment
import com.example.dd1_doan_lan4.DatabaseHelper
import com.example.doan.R
import com.example.doan.adapter.AppointmentListAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class LichHenActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var lvLich: ListView
    private lateinit var bottomNav: BottomNavigationView
    private var appointmentList = ArrayList<Appointment>()
    private lateinit var adapter: AppointmentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_lich_hen)



        // 1. Ánh xạ
        dbHelper = DatabaseHelper(this)
        lvLich = findViewById(R.id.lvLich)
        bottomNav = findViewById(R.id.bottomNav)

        // 2. Cấu hình giao diện ListView
        lvLich.divider = null
        lvLich.dividerHeight = 0

        // 3. Gán Adapter
        adapter = AppointmentListAdapter(
            context = this,
            listData = appointmentList,
            // Xử lý khi bấm nút Sửa
            onEditClick = { itemCanSua ->
                val intent = Intent(this, CapNhatLichHenActivity::class.java)
                intent.putExtra("APPOINTMENT_ID", itemCanSua.id)
                startActivity(intent)
            },
            // Xử lý khi bấm nút Xóa
            onDeleteClick = { itemCanXoa ->
                xacNhanXoa(itemCanXoa)
            }
        )

        lvLich.adapter = adapter

        // 4. Load dữ liệu
        loadData()
        CheckUserRole()
        // --- Bottom Navigation ---
        bottomNav.selectedItemId = R.id.menu_lichKham
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_lichKham -> true
                R.id.menu_pet -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }
                R.id.menu_timKiem -> {
                    startActivity(Intent(this, SearchProfilePetActivity::class.java))
                    true
                }
                R.id.menu_thongKe->{
                    val intent = Intent(this, MangHinhThongKe::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.menu_taiKhoan -> {
                    startActivity(Intent(this, TaiKhoanActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
    private fun CheckUserRole(){
        val sharedPreferences = getSharedPreferences("UserRole",MODE_PRIVATE)
        val role = sharedPreferences.getString("ROLE","USER") //mặc định là user

        if(role == "USER"){
            val menu = bottomNav.menu
            menu.findItem(R.id.menu_pet).isVisible=false
            menu.findItem(R.id.menu_thongKe).isVisible=false
        }
    }

    private fun loadData() {
        appointmentList.clear()
        appointmentList.addAll(dbHelper.getAllAppointments())
        adapter.notifyDataSetChanged()
    }

    private fun xacNhanXoa(appt: Appointment) {
        AlertDialog.Builder(this)
            .setTitle("Xóa lịch hẹn")
            .setMessage("Bạn muốn xóa lịch của ${appt.petName}?")
            .setPositiveButton("Xóa") { _, _ ->
                dbHelper.deleteAppointment(appt.id)
                loadData()
                Toast.makeText(this, "Đã xóa!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}