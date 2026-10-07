package com.example.doan.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.repository.FirebaseBenhAnRepository
import com.example.doan.model.InforBenhAn
import com.example.doan.R

class SuaBenhAnActivity : AppCompatActivity() {
    lateinit var edtNgayKham: EditText
    lateinit var edtTrieuChung: EditText
    lateinit var edtChuanDoan: EditText
    lateinit var edtThuocDieuTri: EditText
    lateinit var btnHuy: Button
    lateinit var btnUpdate: Button
    lateinit var repoBenhAn: FirebaseBenhAnRepository
    
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
        repoBenhAn = FirebaseBenhAnRepository()
        val ba_id = intent.getStringExtra("Ba_ID") ?: ""
        var currentPetID = ""
        
        if (ba_id.isEmpty()) {
            Toast.makeText(this, "Id bệnh án sai", Toast.LENGTH_LONG).show()
            finish()
        } else {
            repoBenhAn.getBenhAnById(ba_id) { benhAnCu ->
                if (benhAnCu != null) {
                    currentPetID = benhAnCu.pet_id
                    edtNgayKham.setText(benhAnCu.ngay)
                    edtTrieuChung.setText(benhAnCu.trieuchung)
                    edtChuanDoan.setText(benhAnCu.chuanDoan)
                    edtThuocDieuTri.setText(benhAnCu.thuoc)
                } else {
                    Toast.makeText(this, "Không tìm thấy bệnh án", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
        
        btnUpdate.setOnClickListener {
            if (edtNgayKham.text.trim().isEmpty()) {
                edtNgayKham.error = "Vui lòng nhập dữ liệu"
                return@setOnClickListener
            }
            if (edtTrieuChung.text.trim().isEmpty()) {
                edtTrieuChung.error = "Vui lòng nhập dữ liệu"
                return@setOnClickListener
            }
            if (edtChuanDoan.text.trim().isEmpty()) {
                edtChuanDoan.error = "Vui lòng nhập dữ liệu"
                return@setOnClickListener
            }
            if (edtThuocDieuTri.text.trim().isEmpty()) {
                edtThuocDieuTri.error = "Vui lòng nhập dữ liệu"
                return@setOnClickListener
            }
            
            val benhanMoi = InforBenhAn(
                id = ba_id,
                pet_id = currentPetID,
                ngay = edtNgayKham.text.toString(),
                trieuchung = edtTrieuChung.text.toString(),
                chuanDoan = edtChuanDoan.text.toString(),
                thuoc = edtThuocDieuTri.text.toString()
            )
            
            repoBenhAn.updateBenhAn(benhanMoi) { success ->
                if (success) {
                    Toast.makeText(this, "Sửa thành công", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Lỗi sửa bệnh án", Toast.LENGTH_LONG).show()
                }
            }
        }
        btnHuy.setOnClickListener {
            finish()
        }
    }
}