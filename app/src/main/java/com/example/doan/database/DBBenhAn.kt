package com.example.doan.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.doan.model.InforBenhAn

class DBBenhAn(context: Context) : SQLiteOpenHelper(context, "dbBenhAn.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val sql =
            "CREATE TABLE BenhAn(id INTEGER PRIMARY KEY AUTOINCREMENT,pet_id INTEGER,ngay TEXT,trieuchung TEXT,chandoan TEXT,thuoc TEXT)"
        db!!.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    fun ThemBenhAn(ba: InforBenhAn) {
        writableDatabase.insert("BenhAn", null, ContentValues().apply {
            put("pet_id", ba.pet_id)
            put("ngay", ba.ngay)
            put("trieuchung", ba.trieuchung)
            put("chandoan", ba.chuanDoan) // ✅ đúng tên cột
            put("thuoc", ba.thuoc)
        })
    }

    fun getBenhAnTheoPet(id: Int): MutableList<InforBenhAn> {
        val list = mutableListOf<InforBenhAn>()
        val query = readableDatabase.rawQuery(
            "SELECT * FROM BenhAn WHERE pet_id= ?",
            arrayOf(id.toString())
        )
        if (query.moveToFirst()) {
            do {
                val ba = InforBenhAn(
                    id = query.getInt(0),
                    pet_id = query.getInt(1),
                    ngay = query.getString(2),
                    trieuchung = query.getString(3),
                    chuanDoan = query.getString(4),
                    thuoc = query.getString(5)
                )
                list.add(ba)
            } while (query.moveToNext())
        }
        query.close()
        return list
    }

    fun SuaBenhAn(ba: InforBenhAn) {
        writableDatabase.update("BenhAn", ContentValues().apply {
            put("pet_id", ba.pet_id)
            put("ngay", ba.ngay)
            put("trieuchung", ba.trieuchung)
            put("chandoan", ba.chuanDoan) // ✅ đúng tên cột
            put("thuoc", ba.thuoc)
        }, "id=?", arrayOf(ba.id.toString()))
    }

    fun getBenhAnID(id: Int): InforBenhAn? {
        val query = readableDatabase.rawQuery(
            "SELECT * FROM BenhAn WHERE id= ?",
            arrayOf(id.toString())
        )
        var ba: InforBenhAn? = null
        if (query.moveToFirst()) {
            ba = InforBenhAn(
                id = query.getInt(0),
                pet_id = query.getInt(1),
                ngay = query.getString(2),
                trieuchung = query.getString(3),
                chuanDoan = query.getString(4),
                thuoc = query.getString(5)
            )
        }
        query.close()
        return ba
    }

    fun XoaBenhAn(id: Int) {
        writableDatabase.delete("BenhAn", "id=?", arrayOf(id.toString()))
    }

    fun filterBenhAnByDateRange(from_ddMMyyyy: String, to_ddMMyyyy: String): MutableList<InforBenhAn> {

        fun toKey(s: String): String {
            if (s.length < 10) return ""
            val dd = s.substring(0, 2)
            val mm = s.substring(3, 5)
            val yy = s.substring(6, 10)
            return yy + mm + dd // yyyyMMdd
        }

        val fromKey = toKey(from_ddMMyyyy)
        val toKey = toKey(to_ddMMyyyy)

        val list = mutableListOf<InforBenhAn>()
        val sql = """
        SELECT * FROM BenhAn
        WHERE (substr(ngay,7,4) || substr(ngay,4,2) || substr(ngay,1,2)) BETWEEN ? AND ?
        ORDER BY (substr(ngay,7,4) || substr(ngay,4,2) || substr(ngay,1,2)) DESC
    """.trimIndent()

        readableDatabase.rawQuery(sql, arrayOf(fromKey, toKey)).use { c ->
            while (c.moveToNext()) {
                list.add(
                    InforBenhAn(
                        id = c.getInt(0),
                        pet_id = c.getInt(1),
                        ngay = c.getString(2),
                        trieuchung = c.getString(3),
                        chuanDoan = c.getString(4),
                        thuoc = c.getString(5)
                    )
                )
            }
        }
        return list
    }
}
