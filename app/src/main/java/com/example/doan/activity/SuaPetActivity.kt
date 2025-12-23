package com.example.doan.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.database.DBpet
import com.example.doan.model.InforPet
import com.example.doan.R

class SuaPetActivity : AppCompatActivity() {
    lateinit var edtname: EditText
    lateinit var edtGiong: EditText
    lateinit var edtCanNang: EditText
    lateinit var edtNote: EditText
    lateinit var btnHuy: Button
    lateinit var btnLuu: Button
    lateinit var dbPet: DBpet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sua_pet)
        setConTrol()
        setEvent()
    }

    private fun setConTrol() {
        edtname = findViewById(R.id.edtName)
        edtGiong = findViewById(R.id.edtBreed)
        edtCanNang = findViewById(R.id.edtWeight)
        edtNote = findViewById(R.id.edtNote)
        btnHuy = findViewById(R.id.btnCancel)
        btnLuu = findViewById(R.id.btnUpdate)
    }

    private fun setEvent() {
        dbPet = DBpet(this)
        val pet_id = intent.getIntExtra("Pet_ID", -1) //lấy id của pet mặc định là -1

        if (pet_id == -1) {
            Toast.makeText(this, "id pet sai sai", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            val pet = dbPet.getpetID(pet_id)
            pet?.let { // code này chỉ chạy khi khác null
                edtname.setText(pet.title)
                edtGiong.setText(pet.breed)
                edtCanNang.setText(pet.weight.toString())
                edtNote.setText(pet.chitiet)
            }
        }

        btnLuu.setOnClickListener {
            val name = edtname.text.trim().toString()
            val giong = edtGiong.text.trim().toString()
            val canNang = edtCanNang.text.trim().toString()
            val GhiChu = edtNote.text.trim().toString()

            if (name.isNullOrEmpty()) {
                edtname.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }
            if (giong.isNullOrEmpty()) {
                edtGiong.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }
            if (canNang.isNullOrEmpty()) {
                edtCanNang.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }

            val pet = InforPet(
                pet_id,
                image = R.drawable.meo1,
                title = name,
                breed = giong,
                weight = canNang.toFloat(),
                chitiet = GhiChu
            )
            dbPet.SuaPet(pet)
            Toast.makeText(this, "sua thanh cong", Toast.LENGTH_LONG).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        btnHuy.setOnClickListener {
            finish()
        }
    }
}