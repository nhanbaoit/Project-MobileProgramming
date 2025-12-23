package com.example.doan.activity

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.database.DBBenhAn
import com.example.doan.R

class ChiTietBenhAnActivity : AppCompatActivity() {
    lateinit var tvNgayKham: TextView
    lateinit var tvTrieuChung: TextView
    lateinit var tvChuanDoan: TextView
    lateinit var tvThuoc: TextView
    lateinit var dbBenhAn: DBBenhAn
    var idBenhAn = -1
    var idPet = -1
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
        dbBenhAn = DBBenhAn(this)
        idBenhAn = intent.getIntExtra("Ba_ID", -1)
        idPet = intent.getIntExtra("Pet_ID", -1)
        if (idBenhAn == -1) {
            Toast.makeText(this, "Id bệnh án hoặc pet sai sai", Toast.LENGTH_LONG).show()
            finish()
        } else {
            val benhAn = dbBenhAn.getBenhAnID(idBenhAn)
            benhAn?.let {
                idPet = benhAn.pet_id
                tvNgayKham.setText("Ngày Khám: "+benhAn.ngay)
                tvTrieuChung.setText("Triệu chứng: "+benhAn.trieuchung)
                tvChuanDoan.setText("Chuẩn đoán: "+benhAn.chuanDoan)
                tvThuoc.setText("Thuốc điều trị: "+benhAn.thuoc)
            }
        }
    }
}