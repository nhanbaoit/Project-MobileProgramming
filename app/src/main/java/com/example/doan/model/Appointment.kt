package com.example.dd1_doan_lan4

data class Appointment(
    var id: Int = 0,         // ID tự tăng (SQLite sẽ lo việc này)
    var petOwnerId: Int,     // ID của thú cưng
    var pet_id:Int,
    var petName: String,     // Tên thú cưng (để hiển thị cho nhanh)
    var serviceType: String, // Loại dịch vụ
    var doctorName: String,  // Tên bác sĩ
    var dateTime: Long,      // Thời gian (Lưu dưới dạng số Long - Milliseconds)
    var isRepeat: Boolean,   // Lặp lại hay không
    var isStatus: Boolean = false
)