package com.pavlovalexey.pleinair.profile.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class ProfileViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val _user = MutableLiveData<FirebaseUser?>().apply {
        value = auth.currentUser
    }
    val user: LiveData<FirebaseUser?> = _user

    fun logout() {
        auth.signOut()
        _user.value = null
    }

    fun updateProfileImageUrl(imageUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .update("profileImageUrl", imageUrl)
            .addOnSuccessListener {
                // URL обновлен успешно
            }
            .addOnFailureListener { e ->
                // Обработка ошибки
            }
    }

    fun uploadImageToFirebase(imageBitmap: Bitmap, onSuccess: (Uri) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef: StorageReference = storage.reference.child("profile_images/$userId/${UUID.randomUUID()}.jpg")

        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // Получение URL старого изображения профиля и его удаление, если оно существует
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val oldImageUrl = document.getString("profileImageUrl")
                if (oldImageUrl != null) {
                    val oldImageRef = storage.getReferenceFromUrl(oldImageUrl)
                    oldImageRef.delete()
                        .addOnFailureListener { e ->
                            // Обработка ошибки удаления старого изображения
                        }
                }

                // Загрузка нового изображения
                storageRef.putBytes(data)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            onSuccess(uri)
                        }
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                // Обработка ошибки при получении документа
                onFailure(e)
            }
    }

    fun uploadImageToFirebase(uri: Uri, onSuccess: (Uri) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef: StorageReference = storage.reference.child("profile_images/$userId/${UUID.randomUUID()}.jpg")

        // Получение URL старого изображения профиля и его удаление, если оно существует
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val oldImageUrl = document.getString("profileImageUrl")
                if (oldImageUrl != null) {
                    val oldImageRef = storage.getReferenceFromUrl(oldImageUrl)
                    oldImageRef.delete()
                        .addOnFailureListener { e ->
                            // Обработка ошибки удаления старого изображения
                        }
                }

                // Загрузка нового изображения
                storageRef.putFile(uri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            onSuccess(uri)
                        }
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                // Обработка ошибки при получении документа
                onFailure(e)
            }
    }
}