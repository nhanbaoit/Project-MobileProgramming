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

class ThemPetActivity : AppCompatActivity() {
    lateinit var edtname: EditText
    lateinit var edtGiong: EditText
    lateinit var edtCanNang: EditText
    lateinit var edtNote: EditText
    lateinit var btnHuy: Button
    lateinit var btnLuu: Button
    lateinit var dbPet: DBpet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thempet)
        setConTrol()
        setEvent()
    }
    private fun setConTrol()
    {
        edtname = findViewById(R.id.edtName)
        edtGiong = findViewById(R.id.edtBreed)
        edtCanNang = findViewById(R.id.edtWeight)
        edtNote = findViewById(R.id.edtNote)
        btnHuy = findViewById(R.id.btnCancel)
        btnLuu = findViewById(R.id.btnSave)
    }
    private fun setEvent(){
        dbPet = DBpet(this)
        btnLuu.setOnClickListener {
            val name = edtname.text.trim().toString()
            val giong = edtGiong.text.trim().toString()
            val canNang = edtCanNang.text.trim().toString()
            val GhiChu = edtNote.text.trim().toString()

            if(name.isNullOrEmpty()){
                edtname.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }
            if(giong.isNullOrEmpty()){
                edtGiong.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }
            if(canNang.isNullOrEmpty()){
                edtCanNang.setError("Vui lòng nhập dữ liệu")
                return@setOnClickListener
            }
            val pet= InforPet(
                0,
                image = R.drawable.meo1,
                title = name,
                breed = giong,
                weight = canNang.toFloat(),
                chitiet = GhiChu
            )
            dbPet.ThemPet(pet)
            Toast.makeText(this,"them thanh cong", Toast.LENGTH_LONG).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        btnHuy.setOnClickListener {
            finish()
        }
    }

}