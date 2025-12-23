package com.example.doan.model

data class PetRecord(
    var maBenhAn: String,
    var tenThuCung: String,
    var chuSoHuu: String,
    var soDienThoai: String,
    var hinh: String
) {
    override fun toString(): String {
        return "$maBenhAn - $tenThuCung"
    }
}

