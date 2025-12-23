package com.example.doan.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.activity.MainActivity
import com.example.doan.R

class DangNhapActivity : AppCompatActivity() {
    lateinit var edtEmail: EditText
    lateinit var edtPassword: EditText
    lateinit var btnDangNhap: Button
    lateinit var tvDangKy: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dang_nhap)
        setConTrol()
        setEvent()
    }

    private fun setConTrol() {
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnDangNhap = findViewById(R.id.btnLogin)
        tvDangKy = findViewById(R.id.tvDangKy)
    }

    private fun setEvent() {
        btnDangNhap.setOnClickListener {
            val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
            val savedEmail = sharedPreferences.getString("email", "")
            val savedPassword = sharedPreferences.getString("password", "")

            //tạo biến lưu quyền ở đây
            val sharedUser = getSharedPreferences("UserRole",MODE_PRIVATE)
            val editor = sharedUser.edit()

            if (edtEmail.text.trim().isEmpty()) {
                edtEmail.setError("Vui lòng nhập email")
                edtEmail.requestFocus()
                return@setOnClickListener
            }
            if (edtPassword.text.trim().isEmpty()) {
                edtPassword.setError("Vui lòng nhập mật khẩu")
                edtPassword.requestFocus()
                return@setOnClickListener
            }
            if (edtEmail.text.toString().trim() == "admin@gmail.com" && edtPassword.text.toString()
                    .trim() == "admin123"
            ) {
                Toast.makeText(this, "Đăng nhập thành công: (admin)", Toast.LENGTH_SHORT).show()
                editor.putString("ROLE","ADMIN")
                editor.apply()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else
                if (edtEmail.text.toString().trim() == savedEmail && edtPassword.text.toString()
                        .trim() == savedPassword
                ) {
                    Toast.makeText(this, "Đăng nhập thành công: (User)", Toast.LENGTH_SHORT).show()

                    //Lưu quyền là user
                    editor.putString("ROLE","USER")
                    editor.apply()
                    val intent = Intent(this, SearchProfilePetActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                }
        }
        tvDangKy.setOnClickListener {
            val intent = Intent(this, DangKyActivity::class.java)
            startActivity(intent)
        }
    }

}