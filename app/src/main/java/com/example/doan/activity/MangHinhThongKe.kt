package com.example.doan.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.R
import com.example.doan.adapter.ThongKeAdapter
import com.example.doan.database.DBThongKe
import com.google.android.material.bottomnavigation.BottomNavigationView

class MangHinhThongKe : AppCompatActivity() {

    private lateinit var lvThongKe: ListView
    private lateinit var dbThongKe: DBThongKe
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mang_hinh_thong_ke)

        // 1. Ánh xạ các thành phần
        lvThongKe = findViewById(R.id.lvThongKe)
        bottomNav = findViewById(R.id.bottomNav)

        // 2. Khởi tạo Database và Load dữ liệu
        dbThongKe = DBThongKe(this)
        loadStatistics()

        // 3. Xử lý sự kiện Click cho Bottom Navigation (Đồng bộ với Main)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_pet -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.menu_timKiem -> {
                    val intent = Intent(this, SearchProfilePetActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.menu_lichKham -> {
                    val intent = Intent(this, DatLichActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.menu_thongKe -> {
                    // Đang ở màn hình Thống kê rồi
                    true
                }
                R.id.menu_taiKhoan -> {
                    val intent = Intent(this, TaiKhoanActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadStatistics() {
        dbThongKe.layDuLieuThongKe { dataList ->
            val adapter = ThongKeAdapter(this@MangHinhThongKe, dataList)
            lvThongKe.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        // Đảm bảo icon Thống kê được sáng lên khi vào màn hình này
        bottomNav.selectedItemId = R.id.menu_thongKe
        loadStatistics()
    }
}