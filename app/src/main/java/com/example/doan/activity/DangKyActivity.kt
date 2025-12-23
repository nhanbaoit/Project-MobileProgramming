package com.example.doan.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.R
import kotlin.text.iterator

class DangKyActivity : AppCompatActivity() {
    lateinit var edtName: EditText
    lateinit var edtEmail: EditText
    lateinit var edtmatKhau: EditText
    lateinit var edtNhapLaiMatKhau: EditText
    lateinit var tvDangNhap: TextView
    lateinit var btnDangKy: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dang_nhap_dang_ky)
        setConTrol()
        setEvent()
    }

    private fun setConTrol() {
        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtmatKhau = findViewById(R.id.edtPassword)
        edtNhapLaiMatKhau = findViewById(R.id.edtPasswordAnother)
        tvDangNhap = findViewById(R.id.tvDangNhap)
        btnDangKy = findViewById(R.id.btnDangKy)
    }

    private fun setEvent() {
        btnDangKy.setOnClickListener {
            //Kiểm tra số lượng ký tự nhập vào
            if (edtName.text.trim().toString().length > 30) {
                edtName.setError("Họ tên không được quá 30 ký tự!")
                edtName.requestFocus()
                return@setOnClickListener
            }
            //kiểm tra họ tên có rỗng không
            if (edtName.text.trim().isEmpty()) {
                edtName.setError("Vui lòng nhập họ tên!")
                edtName.requestFocus()
                return@setOnClickListener
            }
            //kiểm tra dữ liệu nhập vào họ tên có bắt đầu là ký tự là số hay chứa ký tự đặc biệt nào không
            if (!IsvalidName(edtName.text.toString().trim())) {
                edtName.setError("Họ tên không được bắt đầu bằng số, không chứa ký tự đặc biệt!")
                edtName.requestFocus()
                return@setOnClickListener
            }
            //Kiểm tra email có trống không
            if (edtEmail.text.trim().isEmpty()) {
                edtEmail.setError("Vui lòng nhập email!")
                edtEmail.requestFocus()
                return@setOnClickListener
            }
            //Kiểm tra email có kết thúc là @gmail.com hay không
            if (!IsvalidEmail(edtEmail.text.toString().trim())) {
                edtEmail.setError("Email phải có @gmail.com")
                edtEmail.requestFocus()
                return@setOnClickListener
            }
            if (edtmatKhau.text.trim().isEmpty()) {
                edtmatKhau.setError("Vui lòng nhập mật khẩu!")
                edtmatKhau.requestFocus()
                return@setOnClickListener
            }
            //kiểm tra ràng buộc mật khẩu
            if (edtmatKhau.text.trim().toString().length > 16) {
                edtmatKhau.setError("Mật khẩu không được dài hơn 16 ký tự")
                edtmatKhau.requestFocus()
                return@setOnClickListener
            }
            if (edtNhapLaiMatKhau.text.toString().trim()
                    .isEmpty() || edtNhapLaiMatKhau.text.toString()
                    .trim() != edtmatKhau.text.toString().trim()
            ) {
                edtNhapLaiMatKhau.setError("mật khẩu không trùng khớp!")
                edtNhapLaiMatKhau.requestFocus()
                return@setOnClickListener
            }
            //Lưu dữ liệu vào shared Preferences
            val sharedPreferences = getSharedPreferences(
                "User",
                MODE_PRIVATE
            ) // tạo hoặc mở file sharedPreferences với tên gọi là user, chỉ app này mở được,lưu trữ dữ liệu dưới dạng khóa-giá trị
            val editor =
                sharedPreferences.edit() //chỉnh sửa file sharedPreferences thông qua biến editor
            editor.putString(
                "name",
                edtName.text.toString().trim()
            )//lưu chuỗi name với giá trị là edtname
            editor.putString("email", edtEmail.text.toString().trim())
            editor.putString("password", edtmatKhau.text.toString().trim())
            editor.apply()//áp dụng
            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
            //Chuyển sang màn hình đăng nhập
            finish()
        }
        tvDangNhap.setOnClickListener {
            val intent = Intent(this, DangNhapActivity::class.java)
            startActivity(intent)
        }
    }


    //Kiểm tra tên không có số và ký tự đặc biệt(chỉ chấp nhận chữ và khoảng trắng)
    private fun IsvalidName(name: String): Boolean {


        if (!name[0].isLetter()) return false
        for (char in name) {
            if (!char.isLetterOrDigit() && char != ' ') {
                return false
            }
        }
        return true
    }


    //Kiểm tra email có @gmail.com hay không
    private fun IsvalidEmail(email: String): Boolean {
        if (!email.endsWith("@gmail.com"))
            return false
        return true
    }
}
