package com.example.doan.repository

import com.example.doan.model.InforBenhAn
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseBenhAnRepository {
    private val db = FirebaseFirestore.getInstance()
    private val baCollection = db.collection("BenhAn")

    fun addBenhAn(ba: InforBenhAn, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val id = baCollection.document().id
        ba.id = id
        baCollection.document(id).set(ba)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getBenhAnByPet(petId: String, onResult: (MutableList<InforBenhAn>) -> Unit) {
        baCollection.whereEqualTo("pet_id", petId).get()
            .addOnSuccessListener { snapshot ->
                val list = mutableListOf<InforBenhAn>()
                for (doc in snapshot.documents) {
                    val ba = doc.toObject(InforBenhAn::class.java)
                    if (ba != null) list.add(ba)
                }
                onResult(list)
            }
            .addOnFailureListener {
                onResult(mutableListOf())
            }
    }

    fun updateBenhAn(ba: InforBenhAn, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        baCollection.document(ba.id).set(ba)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getBenhAnById(id: String, onResult: (InforBenhAn?) -> Unit) {
        baCollection.document(id).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    onResult(doc.toObject(InforBenhAn::class.java))
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun deleteBenhAn(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        baCollection.document(id).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun filterBenhAnByDateRange(from_ddMMyyyy: String, to_ddMMyyyy: String, onResult: (MutableList<InforBenhAn>) -> Unit) {
        // Thực tế Firebase cần lưu timestamp, nhưng nếu lưu chuỗi ngày thì filter client
        fun toKey(s: String): String {
            if (s.length < 10) return ""
            val dd = s.substring(0, 2)
            val mm = s.substring(3, 5)
            val yy = s.substring(6, 10)
            return yy + mm + dd 
        }
        val fromKey = toKey(from_ddMMyyyy)
        val toKey = toKey(to_ddMMyyyy)

        baCollection.get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<InforBenhAn>()
            for (doc in snapshot.documents) {
                val ba = doc.toObject(InforBenhAn::class.java)
                if (ba != null) {
                    val dKey = toKey(ba.ngay)
                    if (dKey >= fromKey && dKey <= toKey) {
                        list.add(ba)
                    }
                }
            }
            // Sort list
            list.sortByDescending { toKey(it.ngay) }
            onResult(list)
        }.addOnFailureListener {
            onResult(mutableListOf())
        }
    }
}
