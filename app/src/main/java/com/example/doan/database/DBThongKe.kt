package com.example.doan.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.dd1_doan_lan4.DatabaseHelper
import com.google.firebase.firestore.FirebaseFirestore
import com.example.doan.model.ThongKe as ThongKeModel

class DBThongKe(context: Context) {

    private val dbHelper = DatabaseHelper(context)
    private val firestore = FirebaseFirestore.getInstance()

    fun layDuLieuThongKe(onResult: (List<ThongKeModel>) -> Unit) {
        val list = mutableListOf<ThongKeModel>()

        // Lịch hẹn từ SQLite
        val lichXong = demCoDieuKien(dbHelper.readableDatabase, "appointments", "isStatus = 1")
        val lichCho = demCoDieuKien(dbHelper.readableDatabase, "appointments", "isStatus = 0")

        // Truy vấn từ Firebase
        firestore.collection("MyPet").get().addOnSuccessListener { petSnapshot ->
            val tongPet = petSnapshot.size()
            var duc = 0
            var cai = 0
            
            for (doc in petSnapshot) {
                val giong = doc.getString("breed")?.trim()?.lowercase() ?: ""
                if (giong == "duc" || giong == "đực") {
                    duc++
                } else if (giong == "cai" || giong == "cái") {
                    cai++
                }
            }
            
            firestore.collection("BenhAn").get().addOnSuccessListener { baSnapshot ->
                val tongBA = baSnapshot.size()
                
                list.add(ThongKeModel("Tổng số thú cưng", "$tongPet bé"))
                list.add(ThongKeModel("Tổng số hồ sơ bệnh án", "$tongBA hồ sơ"))
                list.add(ThongKeModel("Lịch hẹn đã xong", "$lichXong"))
                list.add(ThongKeModel("Lịch hẹn đang chờ", "$lichCho"))
                list.add(ThongKeModel("Giới tính: Đực", "$duc bé"))
                list.add(ThongKeModel("Giới tính: Cái", "$cai bé"))
                
                onResult(list)
            }.addOnFailureListener {
                // Thêm kết quả tạm nếu lỗi BenhAn
                list.add(ThongKeModel("Tổng số thú cưng", "$tongPet bé"))
                list.add(ThongKeModel("Lịch hẹn đã xong", "$lichXong"))
                list.add(ThongKeModel("Lịch hẹn đang chờ", "$lichCho"))
                list.add(ThongKeModel("Giới tính: Đực", "$duc bé"))
                list.add(ThongKeModel("Giới tính: Cái", "$cai bé"))
                onResult(list)
            }
        }.addOnFailureListener {
            // Lỗi MyPet
            list.add(ThongKeModel("Lịch hẹn đã xong", "$lichXong"))
            list.add(ThongKeModel("Lịch hẹn đang chờ", "$lichCho"))
            onResult(list)
        }
    }

    private fun demCoDieuKien(db: SQLiteDatabase, table: String, condition: String): Int {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $table WHERE $condition", null)
        var count = 0
        if (cursor.moveToFirst()) count = cursor.getInt(0)
        cursor.close()
        return count
    }
}