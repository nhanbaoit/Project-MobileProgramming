package com.example.doan.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class TaiKhoanActivity : AppCompatActivity() {
    lateinit var tvTaiKhoan: TextView
    lateinit var btnDangXuat: Button
    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tai_khoan)
        setConTrol()
        setEvent()
        CheckUserRole()
    }

    private fun setConTrol() {
        tvTaiKhoan = findViewById(R.id.tvTaiKhoan)
        btnDangXuat = findViewById(R.id.btnDangXuat)
        bottomNav = findViewById(R.id.bottomNav)
    }

    private fun setEvent() {
        val sharedPreferences = getSharedPreferences("UserRole", MODE_PRIVATE)
        val role = sharedPreferences.getString("ROLE", "USER")
        if (role == "USER") {
            tvTaiKhoan.text = "Xin Chào User"
        } else {
            tvTaiKhoan.text = "Xin chào Admin"
        }
        btnDangXuat.setOnClickListener {
            val intent = Intent(this, DangNhapActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        bottomNav.selectedItemId = R.id.menu_taiKhoan
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_taiKhoan -> true
                R.id.menu_pet -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                R.id.menu_lichKham -> {
                    val intent = Intent(this, DatLichActivity::class.java)
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

                R.id.menu_thongKe -> {
                    val intent = Intent(this, MangHinhThongKe::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

    }

    private fun CheckUserRole() {
        val sharedPreferences = getSharedPreferences("UserRole", MODE_PRIVATE)
        val role = sharedPreferences.getString("ROLE", "USER") //mặc định là user

        if (role == "USER") {
            val menu = bottomNav.menu
            menu.findItem(R.id.menu_pet).isVisible = false
            menu.findItem(R.id.menu_thongKe).isVisible = false
        }
    }
}