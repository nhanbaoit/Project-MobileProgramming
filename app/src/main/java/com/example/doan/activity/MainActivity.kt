package com.example.doan.activity

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.doan.adapter.CustomAdapter
import com.example.doan.database.DBpet
import com.example.doan.model.InforPet
import com.example.doan.R
import com.example.doan.activity.TaiKhoanActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var customAdapter: CustomAdapter
    private lateinit var lvpet: ListView
    private lateinit var db: DBpet
    private lateinit var themPet: ImageView
    private lateinit var bottomNav: BottomNavigationView
    private var list = mutableListOf<InforPet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setConTrol()
        setEvent()
    }

    private fun setConTrol() {
        lvpet = findViewById(R.id.lvpet)
        themPet = findViewById(R.id.imvAdd)
        bottomNav = findViewById(R.id.bottomNav)
    }

    private fun setEvent() {
        db = DBpet(this)
        list = db.LayDL()

        customAdapter = CustomAdapter(this, list)
        lvpet.adapter = customAdapter

        // Click pet -> chi tiết
        lvpet.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, i, _ ->
                val intent = Intent(this, ChiTietPetActivity::class.java)
                intent.putExtra("Pet_ID", list[i].id)
                startActivity(intent)
            }

        // Thêm pet
        themPet.setOnClickListener {
            startActivity(Intent(this, ThemPetActivity::class.java))
        }

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_pet -> true
                R.id.menu_timKiem -> {
                    val intent = Intent(this, SearchProfilePetActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }

                R.id.menu_lichKham -> {
                    val intent = Intent(this, DatLichActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }

                R.id.menu_taiKhoan -> {
                    val intent = Intent(this, TaiKhoanActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.menu_thongKe->{
                    val intent = Intent(this, MangHinhThongKe::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.menu_taiKhoan -> {
                    val intent = Intent(this, TaiKhoanActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNav.selectedItemId = R.id.menu_pet
        list.clear()
        list.addAll(db.LayDL())
        customAdapter.notifyDataSetChanged()
    }
}