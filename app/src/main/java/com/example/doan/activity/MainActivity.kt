package com.example.doan.activity

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.adapter.CustomAdapter
import com.example.doan.repository.FirebasePetRepository
import com.example.doan.model.InforPet
import com.example.doan.R
import com.example.doan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var customAdapter: CustomAdapter
    private lateinit var repository: FirebasePetRepository
    private var list = mutableListOf<InforPet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = FirebasePetRepository()
        
        customAdapter = CustomAdapter(this, list)
        binding.lvpet.adapter = customAdapter

        setEvent()
        loadData()
    }

    private fun loadData() {
        // Lắng nghe dữ liệu real-time từ Firestore
        repository.listenToPets { updatedList ->
            list.clear()
            list.addAll(updatedList)
            customAdapter.notifyDataSetChanged()
        }
    }

    private fun setEvent() {
        // Click pet -> chi tiết
        binding.lvpet.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, i, _ ->
                val intent = Intent(this, ChiTietPetActivity::class.java)
                intent.putExtra("Pet_ID", list[i].id)
                startActivity(intent)
            }

        // Thêm pet
        binding.imvAdd.setOnClickListener {
            startActivity(Intent(this, ThemPetActivity::class.java))
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_pet -> true
                R.id.menu_timKiem -> {
                    startActivity(Intent(this, SearchProfilePetActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    })
                    true
                }
                R.id.menu_lichKham -> {
                    startActivity(Intent(this, DatLichActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    })
                    true
                }
                R.id.menu_thongKe -> {
                    startActivity(Intent(this, MangHinhThongKe::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    })
                    true
                }
                R.id.menu_taiKhoan -> {
                    startActivity(Intent(this, TaiKhoanActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    })
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNav.selectedItemId = R.id.menu_pet
    }
}