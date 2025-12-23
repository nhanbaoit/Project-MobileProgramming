package com.example.doan.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.dd1_doan_lan4.DatabaseHelper
import com.example.doan.model.ThongKe as ThongKeModel

class DBThongKe(context: Context) {

    private val dbPet = DBpet(context)
    private val dbBenhAn = DBBenhAn(context)
    private val dbHelper = DatabaseHelper(context)

    fun layDuLieuThongKe(): List<ThongKeModel> {
        val list = mutableListOf<ThongKeModel>()

        // 1. Tổng số thú cưng từ bảng Mypet
        val tongPet = demSoLuong(dbPet.readableDatabase, "Mypet")
        list.add(ThongKeModel("Tổng số thú cưng", "$tongPet bé"))

        // 2. Tổng số bệnh án từ bảng BenhAn
        val tongBA = demSoLuong(dbBenhAn.readableDatabase, "BenhAn")
        list.add(ThongKeModel("Tổng số hồ sơ bệnh án", "$tongBA hồ sơ"))

        // 3. Lịch hẹn hoàn thành (isStatus = 1)
        val lichXong = demCoDieuKien(dbHelper.readableDatabase, "appointments", "isStatus = 1")
        list.add(ThongKeModel("Lịch hẹn đã xong", "$lichXong"))

        // 4. Lịch hẹn chưa xong (isStatus = 0)
        val lichCho = demCoDieuKien(dbHelper.readableDatabase, "appointments", "isStatus = 0")
        list.add(ThongKeModel("Lịch hẹn đang chờ", "$lichCho"))

        // 5. Thống kê giới tính (Lọc từ cột 'giong' trong Mypet)
        list.add(ThongKeModel("Giới tính: Đực", "${demGioiTinh("duc")} bé"))
        list.add(ThongKeModel("Giới tính: Cái", "${demGioiTinh("cai")} bé"))

        return list
    }

    private fun demSoLuong(db: SQLiteDatabase, table: String): Int {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $table", null)
        var count = 0
        if (cursor.moveToFirst()) count = cursor.getInt(0)
        cursor.close()
        return count
    }

    private fun demCoDieuKien(db: SQLiteDatabase, table: String, condition: String): Int {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $table WHERE $condition", null)
        var count = 0
        if (cursor.moveToFirst()) count = cursor.getInt(0)
        cursor.close()
        return count
    }

    private fun demGioiTinh(keyword: String): Int {
        val db = dbPet.readableDatabase
        // Sử dụng LOWER và TRIM để đảm bảo độ chính xác khi lọc chuỗi
        val cursor = db.rawQuery("SELECT COUNT(*) FROM Mypet WHERE LOWER(TRIM(giong)) = ?", arrayOf(keyword))
        var count = 0
        if (cursor.moveToFirst()) count = cursor.getInt(0)
        cursor.close()
        return count
    }
}