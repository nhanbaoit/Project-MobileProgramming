package com.example.dd1_doan_lan4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.doan.model.InforPet
import java.util.Calendar

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "dbPet.db", null, 1) {

    companion object {
        // --- 1. CẤU HÌNH BẢNG APPOINTMENTS (LỊCH HẸN) ---
        const val TABLE_APPOINTMENTS = "appointments"
        const val KEY_ID = "id"
        const val KEY_PET_OWNER_ID = "petOwnerId"
        const val KEY_PET_ID_FK = "pet_id"
        const val KEY_SERVICE = "serviceType"
        const val KEY_DOCTOR = "doctorName"
        const val KEY_DATE = "dateTime"
        const val KEY_REPEAT = "isRepeat"

        // ✅ NEW: trạng thái hoàn thành
        const val KEY_STATUS = "isStatus" // 0 = chưa hoàn thành, 1 = hoàn thành

        // --- 2. CẤU HÌNH BẢNG PETS (THÚ CƯNG) ---
        const val TABLE_PETS = "Mypet"
        const val KEY_ID_PET = "id"
        const val KEY_HINH = "hinh"
        const val KEY_TEN = "ten"
        const val KEY_GIONG = "giong"
        const val KEY_CAN_NANG = "canNang"
        const val KEY_CHI_TIET = "chitiet"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // A. Tạo bảng Appointments (✅ có isStatus)
        val createApptTable = """
            CREATE TABLE $TABLE_APPOINTMENTS (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_PET_OWNER_ID INTEGER,
                $KEY_PET_ID_FK INTEGER,
                $KEY_SERVICE TEXT,
                $KEY_DOCTOR TEXT,
                $KEY_DATE INTEGER,
                $KEY_REPEAT INTEGER,
                $KEY_STATUS INTEGER DEFAULT 0,
                FOREIGN KEY($KEY_PET_ID_FK) REFERENCES $TABLE_PETS($KEY_ID_PET)
            )
        """.trimIndent()
        db.execSQL(createApptTable)

        // B. Tạo bảng Pets
        val createPetTable = """
            CREATE TABLE $TABLE_PETS (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_HINH INTEGER,
                $KEY_TEN TEXT,
                $KEY_GIONG TEXT,
                $KEY_CAN_NANG FLOAT,
                $KEY_CHI_TIET TEXT
            )
        """.trimIndent()
        db.execSQL(createPetTable)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        if (db != null && !db.isReadOnly) {
            // đảm bảo bảng appointments tồn tại (phòng trường hợp db cũ thiếu bảng)
            val createApptTable = """
                CREATE TABLE IF NOT EXISTS $TABLE_APPOINTMENTS (
                    $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $KEY_PET_OWNER_ID INTEGER,
                    $KEY_PET_ID_FK INTEGER,
                    $KEY_SERVICE TEXT,
                    $KEY_DOCTOR TEXT,
                    $KEY_DATE INTEGER,
                    $KEY_REPEAT INTEGER,
                    $KEY_STATUS INTEGER DEFAULT 0,
                    FOREIGN KEY($KEY_PET_ID_FK) REFERENCES $TABLE_PETS($KEY_ID_PET)
                )
            """.trimIndent()
            db.execSQL(createApptTable)

            val createPetTable = """
                CREATE TABLE IF NOT EXISTS $TABLE_PETS (
                    $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $KEY_HINH INTEGER,
                    $KEY_TEN TEXT,
                    $KEY_GIONG TEXT,
                    $KEY_CAN_NANG FLOAT,
                    $KEY_CHI_TIET TEXT
                )
            """.trimIndent()
            db.execSQL(createPetTable)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Bạn cài lại app nên đoạn này không quan trọng.
        // Giữ cách cũ: drop và tạo lại cho đơn giản.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APPOINTMENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PETS")
        onCreate(db)
    }

    // ==========================================
    // PHẦN 1: XỬ LÝ LỊCH HẸN (APPOINTMENT)
    // ==========================================

    fun insertAppointment(appt: Appointment): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_PET_OWNER_ID, appt.petOwnerId)
            put(KEY_PET_ID_FK, appt.pet_id)
            put(KEY_SERVICE, appt.serviceType)
            put(KEY_DOCTOR, appt.doctorName)
            put(KEY_DATE, appt.dateTime)
            put(KEY_REPEAT, if (appt.isRepeat) 1 else 0)

            // ✅ NEW
            put(KEY_STATUS, if (appt.isStatus) 1 else 0)
        }
        return db.insert(TABLE_APPOINTMENTS, null, values)
    }

    fun getAllAppointments(): List<Appointment> {
        val list = ArrayList<Appointment>()
        val db = readableDatabase

        val sql = """
        SELECT a.*, p.ten AS petName
        FROM appointments a
        LEFT JOIN Mypet p ON a.pet_id = p.id
        ORDER BY a.dateTime DESC
    """.trimIndent()

        val cursor = db.rawQuery(sql, null)
        cursor.use {
            while (it.moveToNext()) {
                val appt = Appointment(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    petOwnerId = it.getInt(it.getColumnIndexOrThrow("petOwnerId")),
                    pet_id = it.getInt(it.getColumnIndexOrThrow("pet_id")),
                    petName = it.getString(it.getColumnIndexOrThrow("petName")) ?: "(Không rõ)",
                    serviceType = it.getString(it.getColumnIndexOrThrow("serviceType")),
                    doctorName = it.getString(it.getColumnIndexOrThrow("doctorName")),
                    dateTime = it.getLong(it.getColumnIndexOrThrow("dateTime")),
                    isRepeat = it.getInt(it.getColumnIndexOrThrow("isRepeat")) == 1,
                    isStatus = it.getInt(it.getColumnIndexOrThrow("isStatus")) == 1
                )
                list.add(appt)
            }
        }
        return list
    }


    fun getAppointmentById(id: Int): Appointment? {
        val db = readableDatabase
        val sql = """
        SELECT a.*, p.ten AS petName
        FROM appointments a
        LEFT JOIN Mypet p ON a.pet_id = p.id
        WHERE a.id = ?
    """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(id.toString()))
        return cursor.use {
            if (it.moveToFirst()) {
                Appointment(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    petOwnerId = it.getInt(it.getColumnIndexOrThrow("petOwnerId")),
                    pet_id = it.getInt(it.getColumnIndexOrThrow("pet_id")),
                    petName = it.getString(it.getColumnIndexOrThrow("petName")) ?: "(Không rõ)",
                    serviceType = it.getString(it.getColumnIndexOrThrow("serviceType")),
                    doctorName = it.getString(it.getColumnIndexOrThrow("doctorName")),
                    dateTime = it.getLong(it.getColumnIndexOrThrow("dateTime")),
                    isRepeat = it.getInt(it.getColumnIndexOrThrow("isRepeat")) == 1,
                    isStatus = it.getInt(it.getColumnIndexOrThrow("isStatus")) == 1
                )
            } else null
        }
    }


    fun updateAppointment(appt: Appointment): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_PET_OWNER_ID, appt.petOwnerId)
            put(KEY_PET_ID_FK, appt.pet_id)
            put(KEY_SERVICE, appt.serviceType)
            put(KEY_DOCTOR, appt.doctorName)
            put(KEY_DATE, appt.dateTime)
            put(KEY_REPEAT, if (appt.isRepeat) 1 else 0)

            // ✅ NEW
            put(KEY_STATUS, if (appt.isStatus) 1 else 0)
        }
        return db.update(TABLE_APPOINTMENTS, values, "$KEY_ID = ?", arrayOf(appt.id.toString()))
    }
    fun deleteAppointment(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_APPOINTMENTS, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
        return success
    }

    /**
     * ✅ NEW: Search lịch khám theo 2 filter:
     * 1) ngày (lấy start/end của ngày)
     * 2) trạng thái: -1 tất cả, 0 chưa hoàn thành, 1 đã hoàn thành
     */
    fun searchAppointmentsByDayAndStatus(dayMillis: Long, statusFilter: Int): List<Appointment> {
        val list = ArrayList<Appointment>()
        val db = readableDatabase

        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = dayMillis
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
        cal.set(java.util.Calendar.MINUTE, 59)
        cal.set(java.util.Calendar.SECOND, 59)
        cal.set(java.util.Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        val where = StringBuilder("a.dateTime BETWEEN ? AND ?")
        val args = mutableListOf(start.toString(), end.toString())

        if (statusFilter != -1) {
            where.append(" AND a.isStatus = ?")
            args.add(statusFilter.toString())
        }

        val sql = """
        SELECT a.*, p.ten AS petName
        FROM appointments a
        LEFT JOIN Mypet p ON a.pet_id = p.id
        WHERE $where
        ORDER BY a.dateTime DESC
    """.trimIndent()

        val cursor = db.rawQuery(sql, args.toTypedArray())
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Appointment(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        petOwnerId = it.getInt(it.getColumnIndexOrThrow("petOwnerId")),
                        pet_id = it.getInt(it.getColumnIndexOrThrow("pet_id")),
                        petName = it.getString(it.getColumnIndexOrThrow("petName")) ?: "(Không rõ)",
                        serviceType = it.getString(it.getColumnIndexOrThrow("serviceType")),
                        doctorName = it.getString(it.getColumnIndexOrThrow("doctorName")),
                        dateTime = it.getLong(it.getColumnIndexOrThrow("dateTime")),
                        isRepeat = it.getInt(it.getColumnIndexOrThrow("isRepeat")) == 1,
                        isStatus = it.getInt(it.getColumnIndexOrThrow("isStatus")) == 1
                    )
                )
            }
        }
        return list
    }

    fun searchAppointmentsByStatus(statusFilter: Int): List<Appointment> {
        val list = ArrayList<Appointment>()
        val db = readableDatabase

        val sql = """
        SELECT a.*, p.ten AS petName
        FROM appointments a
        LEFT JOIN Mypet p ON a.pet_id = p.id
        WHERE a.isStatus = ?
        ORDER BY a.dateTime DESC
    """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(statusFilter.toString()))
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Appointment(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        petOwnerId = it.getInt(it.getColumnIndexOrThrow("petOwnerId")),
                        pet_id = it.getInt(it.getColumnIndexOrThrow("pet_id")),
                        petName = it.getString(it.getColumnIndexOrThrow("petName")) ?: "(Không rõ)",
                        serviceType = it.getString(it.getColumnIndexOrThrow("serviceType")),
                        doctorName = it.getString(it.getColumnIndexOrThrow("doctorName")),
                        dateTime = it.getLong(it.getColumnIndexOrThrow("dateTime")),
                        isRepeat = it.getInt(it.getColumnIndexOrThrow("isRepeat")) == 1,
                        isStatus = it.getInt(it.getColumnIndexOrThrow("isStatus")) == 1
                    )
                )
            }
        }
        return list
    }




    // ==========================================
    // PHẦN 2: XỬ LÝ THÚ CƯNG (PET)
    // ==========================================

    fun insertPet(pet: InforPet): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_HINH, pet.image)
            put(KEY_TEN, pet.title)
            put(KEY_GIONG, pet.breed)
            put(KEY_CAN_NANG, pet.weight)
            put(KEY_CHI_TIET, pet.chitiet)
        }
        return db.insert(TABLE_PETS, null, values)
    }

    fun getAllPets(): List<InforPet> {
        val list = ArrayList<InforPet>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PETS", null)

        cursor.use {
            while (it.moveToNext()) {
                val pet = InforPet(
                    id = it.getInt(it.getColumnIndexOrThrow(KEY_ID)),
                    image = it.getInt(it.getColumnIndexOrThrow(KEY_HINH)),
                    title = it.getString(it.getColumnIndexOrThrow(KEY_TEN)),
                    breed = it.getString(it.getColumnIndexOrThrow(KEY_GIONG)),
                    weight = it.getFloat(it.getColumnIndexOrThrow(KEY_CAN_NANG)),
                    chitiet = it.getString(it.getColumnIndexOrThrow(KEY_CHI_TIET))
                )
                list.add(pet)
            }
        }
        return list
    }
}
