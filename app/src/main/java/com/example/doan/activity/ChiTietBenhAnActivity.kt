package com.example.doan.activity

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.repository.FirebaseBenhAnRepository
import com.example.doan.R

class ChiTietBenhAnActivity : AppCompatActivity() {
    lateinit var tvNgayKham: TextView
    lateinit var tvTrieuChung: TextView
    lateinit var tvChuanDoan: TextView
    lateinit var tvThuoc: TextView
    lateinit var repoBenhAn: FirebaseBenhAnRepository
    var idBenhAn = ""
    var idPet = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chi_tiet_benh_an)
        setConTrol()
        setEvent()
    }

    private fun setConTrol() {
        tvNgayKham = findViewById(R.id.tvNgayKham)
        tvTrieuChung = findViewById(R.id.tvTrieuChung)
        tvChuanDoan = findViewById(R.id.tvChanDoan)
        tvThuoc = findViewById(R.id.tvThuoc)
    }

    private fun setEvent() {
        repoBenhAn = FirebaseBenhAnRepository()
        idBenhAn = intent.getStringExtra("Ba_ID") ?: ""
        idPet = intent.getStringExtra("Pet_ID") ?: ""
        
        if (idBenhAn.isEmpty()) {
            Toast.makeText(this, "Id bệnh án sai", Toast.LENGTH_LONG).show()
            finish()
        } else {
            repoBenhAn.getBenhAnById(idBenhAn) { benhAn ->
                if (benhAn != null) {
                    idPet = benhAn.pet_id
                    tvNgayKham.text = "Ngày Khám: " + benhAn.ngay
                    tvTrieuChung.text = "Triệu chứng: " + benhAn.trieuchung
                    tvChuanDoan.text = "Chuẩn đoán: " + benhAn.chuanDoan
                    tvThuoc.text = "Thuốc điều trị: " + benhAn.thuoc
                } else {
                    Toast.makeText(this, "Không tìm thấy bệnh án", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }
}