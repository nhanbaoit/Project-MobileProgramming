package com.example.doan.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.database.DBBenhAn
import com.example.doan.model.InforBenhAn
import com.example.doan.R

class SuaBenhAnActivity : AppCompatActivity() {
    lateinit var edtNgayKham: EditText
    lateinit var edtTrieuChung: EditText
    lateinit var edtChuanDoan: EditText
    lateinit var edtThuocDieuTri: EditText
    lateinit var btnHuy: Button
    lateinit var btnUpdate: Button
    lateinit var dbBenhAn: DBBenhAn
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sua_benh_an)
        setConTrol()
        setEvent()
    }

    private fun setConTrol() {
        edtNgayKham = findViewById(R.id.edtNgayKham)
        edtTrieuChung = findViewById(R.id.edtTrieuChung)
        edtChuanDoan = findViewById(R.id.edtChanDoan)
        edtThuocDieuTri = findViewById(R.id.edtThuoc)
        btnHuy = findViewById(R.id.btnCancel)
        btnUpdate = findViewById(R.id.btnUpdate)
    }

    private fun setEvent() {
        dbBenhAn = DBBenhAn(this)
        var ba_id = intent.getIntExtra("Ba_ID", -1)
        var currentPetID = -1
        if (ba_id == -1) {
            Toast.makeText(this, "Id bệnh án sai sai", Toast.LENGTH_LONG).show()
            finish()
        } else {
            val benhAnCu = dbBenhAn.getBenhAnID(ba_id)
            benhAnCu?.let {
                currentPetID = benhAnCu.pet_id
                edtNgayKham.setText(benhAnCu.ngay)
                edtTrieuChung.setText(benhAnCu.trieuchung)
                edtChuanDoan.setText(benhAnCu.chuanDoan)
                edtThuocDieuTri.setText(benhAnCu.thuoc)
            }
        }
        btnUpdate.setOnClickListener {
            if (edtNgayKham.text.trim().isNullOrEmpty()) {
                edtNgayKham.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }
            if (edtTrieuChung.text.trim().isNullOrEmpty()) {
                edtTrieuChung.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }
            if (edtChuanDoan.text.trim().isNullOrEmpty()) {
                edtChuanDoan.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }
            if (edtThuocDieuTri.text.trim().isNullOrEmpty()) {
                edtThuocDieuTri.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }
            val benhanMoi = InforBenhAn(
                ba_id,
                currentPetID,
                edtNgayKham.text.toString(),
                edtTrieuChung.text.toString(),
                edtChuanDoan.text.toString(),
                edtThuocDieuTri.text.toString()
            )
            dbBenhAn.SuaBenhAn(benhanMoi)
            Toast.makeText(this, "Sửa thành công", Toast.LENGTH_LONG).show()
            finish()
        }
        btnHuy.setOnClickListener {
            finish()
        }
    }
}