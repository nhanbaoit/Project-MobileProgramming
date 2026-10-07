package com.example.doan.repository

import com.example.doan.model.InforPet
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FirebasePetRepository {
    private val db = FirebaseFirestore.getInstance()
    private val petCollection = db.collection("MyPet")

    fun addPet(pet: InforPet, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val id = petCollection.document().id // Tạo ID mới tự động
        pet.id = id
        petCollection.document(id).set(pet)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun updatePet(pet: InforPet, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        petCollection.document(pet.id).set(pet)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun deletePet(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        petCollection.document(id).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getPetById(id: String, onResult: (InforPet?) -> Unit) {
        petCollection.document(id).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val pet = document.toObject(InforPet::class.java)
                    onResult(pet)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun listenToPets(onDataChanged: (MutableList<InforPet>) -> Unit) {
        // Lắng nghe real-time từ Firestore
        petCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = mutableListOf<InforPet>()
                for (doc in snapshot.documents) {
                    val pet = doc.toObject(InforPet::class.java)
                    if (pet != null) {
                        list.add(pet)
                    }
                }
                onDataChanged(list)
            }
        }
    }
    
    fun searchPets(keyword: String, onResult: (MutableList<InforPet>) -> Unit) {
        // Firestore không hỗ trợ truy vấn chuỗi con kiểu 'LIKE %keyword%' dễ dàng.
        // Giải pháp đơn giản: Lấy tất cả hoặc lọc ở client, vì dữ liệu nhỏ có thể lấy hết rồi lọc
        petCollection.get().addOnSuccessListener { snapshot ->
            val kw = keyword.lowercase()
            val list = mutableListOf<InforPet>()
            for (doc in snapshot.documents) {
                val pet = doc.toObject(InforPet::class.java)
                if (pet != null && (pet.title.lowercase().contains(kw) || pet.breed.lowercase().contains(kw))) {
                    list.add(pet)
                }
            }
            onResult(list)
        }.addOnFailureListener {
            onResult(mutableListOf())
        }
    }
}
