package com.example.doan.model

data class Pet(
    var id: Int = 0, // SQLite tự tăng ID, khi tạo mới để 0 hoặc null
    var ten: String,
    var giong: String,
    var can_nang: Float = 0f

)
