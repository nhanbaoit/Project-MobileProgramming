package com.example.doan.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.repository.FirebasePetRepository
import com.example.doan.model.InforPet
import com.example.doan.R

class SuaPetActivity : AppCompatActivity() {
    lateinit var edtname: EditText
    lateinit var edtGiong: EditText
    lateinit var edtCanNang: EditText
    lateinit var edtNote: EditText
    lateinit var btnHuy: Button
    lateinit var btnLuu: Button
    lateinit var repo: FirebasePetRepository
    
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
        repo = FirebasePetRepository()
        val pet_id = intent.getStringExtra("Pet_ID") // lấy id chuỗi

        if (pet_id.isNullOrEmpty()) {
            Toast.makeText(this, "id pet sai", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            repo.getPetById(pet_id) { pet ->
                pet?.let {
                    edtname.setText(pet.title)
                    edtGiong.setText(pet.breed)
                    edtCanNang.setText(pet.weight.toString())
                    edtNote.setText(pet.chitiet)
                } ?: run {
                    Toast.makeText(this, "Không tìm thấy thú cưng", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        btnLuu.setOnClickListener {
            val name = edtname.text.trim().toString()
            val giong = edtGiong.text.trim().toString()
            val canNang = edtCanNang.text.trim().toString()
            val GhiChu = edtNote.text.trim().toString()

            if (name.isNullOrEmpty()) {
                edtname.error = "Vui lòng nhập dữ liệu"
                return@setOnClickListener
            }
            if (giong.isNullOrEmpty()) {
                edtGiong.error = "Vui lòng nhập dữ liệu"
                return@setOnClickListener
            }
            if (canNang.isNullOrEmpty()) {
                edtCanNang.error = "Vui lòng nhập dữ liệu"
                return@setOnClickListener
            }

            val pet = InforPet(
                id = pet_id,
                image = R.drawable.meo1, // you can keep previous image if needed, but the original did this
                title = name,
                breed = giong,
                weight = canNang.toFloat(),
                chitiet = GhiChu
            )
            
            repo.updatePet(pet, {
                Toast.makeText(this, "sửa thành công", Toast.LENGTH_LONG).show()
                finish()
            }, {
                Toast.makeText(this, "sửa thất bại", Toast.LENGTH_LONG).show()
            })
        }
        
        btnHuy.setOnClickListener {
            finish()
        }
    }
}