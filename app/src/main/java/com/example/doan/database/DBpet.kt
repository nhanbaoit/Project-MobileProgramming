package com.example.doan.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.doan.model.InforPet

class DBpet(context: Context) : SQLiteOpenHelper(context, "dbPet.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val sql =
            "CREATE TABLE Mypet(id INTEGER PRIMARY KEY AUTOINCREMENT,hinh INT, ten TEXT,giong TEXT,canNang FLOAT, chitiet TEXT)"
        db!!.execSQL(sql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {


    }


    fun ThemPet(pet: InforPet) {
        writableDatabase.insert("Mypet", null, ContentValues().apply {
            put("hinh", pet.image)
            put("ten", pet.title)
            put("giong", pet.breed)
            put("canNang", pet.weight)
            put("chitiet", pet.chitiet)
        })
    }

    fun SuaPet(pet: InforPet) {
        writableDatabase.update("Mypet", ContentValues().apply {
            put("hinh", pet.image)
            put("ten", pet.title)
            put("giong", pet.breed)
            put("canNang", pet.weight)
            put("chitiet", pet.chitiet)
        }, "id=?", arrayOf(pet.id.toString()))
    }

    fun getpetID(id: Int): InforPet? {
        val query = readableDatabase.rawQuery(
            "SELECT * FROM MyPet WHERE id= ?",
            arrayOf(id.toString())
        ) //tự tạo câu truy vấn
        var pet: InforPet? = null
        if (query.moveToFirst()) {   //Lấy dòng dữ liệu đầu tiên theo câu truy vấn đã viết ở trên , (movetoNext laf lấy nhiều dòng))
            pet = InforPet(
                id = query.getInt(0),
                image = query.getInt(1),
                title = query.getString(2),
                breed = query.getString(3),
                weight = query.getFloat(4),
                chitiet = query.getString(5)
            )
        }
        query.close()
        return pet
    }

    fun XoaPet(id: Int) {
        writableDatabase.delete("Mypet", "id=?", arrayOf(id.toString()))
    }


    fun LayDL(): MutableList<InforPet> {
        val list = mutableListOf<InforPet>()
        readableDatabase.rawQuery("SELECT * FROM Mypet", null).use {
            while (it.moveToNext())
                list.add(
                    InforPet(
                        it.getInt(0),
                        it.getInt(1),
                        it.getString(2),
                        it.getString(3),
                        it.getFloat(4),
                        it.getString(5)
                    )
                )
        }
        return list
    }

    fun searchPet(keyword: String): MutableList<InforPet> {
        val list = mutableListOf<InforPet>()
        val kw = "%${keyword.trim()}%"
        val sql = "SELECT * FROM Mypet WHERE ten LIKE ? OR giong LIKE ? ORDER BY id DESC"

        readableDatabase.rawQuery(sql, arrayOf(kw, kw)).use {
            while (it.moveToNext()) {
                list.add(
                    InforPet(
                        id = it.getInt(0),
                        image = it.getInt(1),
                        title = it.getString(2),
                        breed = it.getString(3),
                        weight = it.getFloat(4),
                        chitiet = it.getString(5)
                    )
                )
            }
        }
        return list
    }

}