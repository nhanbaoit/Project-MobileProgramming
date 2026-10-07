package com.example.doan.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.repository.FirebaseBenhAnRepository
import com.example.doan.model.InforBenhAn
import com.example.doan.R

class ThemBenhAnActivity : AppCompatActivity() {
    lateinit var edtNgayKham: EditText
    lateinit var edtTrieuChung: EditText
    lateinit var edtChuanDoan: EditText
    lateinit var edtThuocDieuTri: EditText
    lateinit var btnHuy: Button
    lateinit var btnLuu: Button
    lateinit var repoBenhAn: FirebaseBenhAnRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_them_benh_an)
        setConTrol()
        setEvent()
    }

    private fun setConTrol() {
        edtNgayKham = findViewById(R.id.edtNgayKham)
        edtTrieuChung = findViewById(R.id.edtTrieuChung)
        edtChuanDoan = findViewById(R.id.edtChanDoan)
        edtThuocDieuTri = findViewById(R.id.edtThuoc)
        btnHuy = findViewById(R.id.btnCancel)
        btnLuu = findViewById(R.id.btnSave)
    }

    private fun setEvent() {
        repoBenhAn = FirebaseBenhAnRepository()
        val pet_id = intent.getStringExtra("Pet_ID") ?: ""
        
        btnLuu.setOnClickListener {
            val ngayKham = edtNgayKham.text.toString().trim()
            val trieuChung = edtTrieuChung.text.toString().trim()
            val chuanDoan = edtChuanDoan.text.toString().trim()
            val thuoc = edtThuocDieuTri.text.toString().trim()
            
            if (ngayKham.isEmpty()) {
                edtNgayKham.error = "Vui lòng nhập dữ liệu"
                return@setOnClickListener
            }
            if (trieuChung.isEmpty()) {
                edtTrieuChung.error = "Vui lòng nhập dữ liệu"
                return@setOnClickListener
            }
            if (chuanDoan.isEmpty()) {
                edtChuanDoan.error = "Vui lòng nhập dữ liệu"
                return@setOnClickListener
            }
            if (thuoc.isEmpty()) {
                edtThuocDieuTri.error = "Vui lòng nhập dữ liệu"
                return@setOnClickListener
            }
            
            val ba = InforBenhAn(
                pet_id = pet_id,
                ngay = ngayKham,
                trieuchung = trieuChung,
                chuanDoan = chuanDoan,
                thuoc = thuoc
            )
            
            repoBenhAn.addBenhAn(ba) { success ->
                if (success) {
                    Toast.makeText(this, "Thêm thành công", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Lỗi thêm bệnh án", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnHuy.setOnClickListener {
            finish()
        }
    }
}