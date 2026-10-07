package com.example.doan.activity

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.adapter.CustomAdapterBenhAn
import com.example.doan.repository.FirebaseBenhAnRepository
import com.example.doan.repository.FirebasePetRepository
import com.example.doan.model.InforBenhAn
import com.example.doan.R

class ChiTietPetActivity : AppCompatActivity() {
    lateinit var repoPet: FirebasePetRepository
    lateinit var repoBenhAn: FirebaseBenhAnRepository
    lateinit var tvTieuDe: TextView
    lateinit var imgMeo : ImageView
    lateinit var tvtenPet: TextView
    lateinit var tvGiong: TextView
    lateinit var tvCanNang: TextView
    lateinit var tvGhiChu: TextView
    lateinit var tvAddBenhAn : TextView
    var list = mutableListOf<InforBenhAn>()
    lateinit var customAdapterBenhAn: CustomAdapterBenhAn
    lateinit var lvBenhAn : ListView

    var pet_id: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chi_tiet_pet)
        setConTrol()
        setEvent()
    }
    private fun setConTrol(){
        tvTieuDe = findViewById(R.id.tvPetNameTiTle)
        tvtenPet = findViewById(R.id.tvtenPet)
        imgMeo = findViewById(R.id.imgMeo)
        tvGiong = findViewById(R.id.tvGiong)
        tvCanNang = findViewById(R.id.tvCanNang)
        tvGhiChu = findViewById(R.id.tvGhiChu)
        tvAddBenhAn = findViewById(R.id.tvAddBenhAn)
        lvBenhAn = findViewById(R.id.lvbenhAn)
    }
    private fun setEvent(){
        repoPet = FirebasePetRepository()
        repoBenhAn = FirebaseBenhAnRepository()

        pet_id = intent.getStringExtra("Pet_ID")
        if (pet_id != null) {
            repoPet.getPetById(pet_id!!) { pet ->
                pet?.let {
                    tvTieuDe.text = "${it.title}"
                    imgMeo.setImageResource(it.image)
                    tvtenPet.text = "Tên:  ${it.title}"
                    tvGiong.text = "Giống: ${it.breed}"
                    tvCanNang.text = "Cân nặng: ${it.weight} kg"
                    tvGhiChu.text = "Ghi chú(nếu có):  ${it.chitiet}"
                }
            }
        }

        lvBenhAn.onItemClickListener = AdapterView.OnItemClickListener{adapterView,view, i, lng ->
            val intent = Intent(this, ChiTietBenhAnActivity::class.java)
            intent.putExtra("Ba_ID",list[i].id)
            intent.putExtra("Pet_ID",list[i].pet_id)
            startActivity(intent)
        }
        tvAddBenhAn.setOnClickListener {
            val intent = Intent(this,ThemBenhAnActivity::class.java)
            intent.putExtra("Pet_ID", pet_id)
            startActivity(intent)
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadDSBenhAn()
    }
    
    private fun loadDSBenhAn(){
        if (pet_id != null) {
            repoBenhAn.getBenhAnByPet(pet_id!!) { result ->
                list.clear()
                list.addAll(result)
                customAdapterBenhAn = CustomAdapterBenhAn(this@ChiTietPetActivity, list)
                lvBenhAn.adapter = customAdapterBenhAn
            }
        }
    }
}