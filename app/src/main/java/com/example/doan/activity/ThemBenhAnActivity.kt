package com.example.doan.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.database.DBBenhAn
import com.example.doan.model.InforBenhAn
import com.example.doan.R

class ThemBenhAnActivity : AppCompatActivity() {
    lateinit var edtNgayKham: EditText
    lateinit var edtTrieuChung: EditText
    lateinit var edtChuanDoan: EditText
    lateinit var edtThuocDieuTri: EditText
    lateinit var btnHuy: Button
    lateinit var btnLuu: Button
    lateinit var dbBenhAn: DBBenhAn
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
        dbBenhAn = DBBenhAn(this)
        val pet_id =intent.getIntExtra("Pet_ID",-1)
        btnLuu.setOnClickListener {
            var ngayKham = edtNgayKham.text.toString().trim()
            var trieuChung = edtTrieuChung.text.toString().trim()
            var ChuanDoan = edtChuanDoan.text.toString().trim()
            var Thuoc = edtThuocDieuTri.text.toString().trim()
            if (ngayKham.isNullOrEmpty()) {
                edtNgayKham.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }
            if (trieuChung.isNullOrEmpty()) {
                edtTrieuChung.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }
            if (ChuanDoan.isNullOrEmpty()) {
                edtChuanDoan.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }
            if (Thuoc.isNullOrEmpty()) {
                edtThuocDieuTri.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }
            val ba = InforBenhAn(
                0,
                pet_id = pet_id,
                ngay = ngayKham,
                trieuchung = trieuChung,
                chuanDoan = ChuanDoan,
                thuoc = Thuoc
            )
            dbBenhAn.ThemBenhAn(ba)
            Toast.makeText(this, "them thanh cong", Toast.LENGTH_LONG).show()
            finish()

        }

        btnHuy.setOnClickListener {
            finish()
        }
    }
}