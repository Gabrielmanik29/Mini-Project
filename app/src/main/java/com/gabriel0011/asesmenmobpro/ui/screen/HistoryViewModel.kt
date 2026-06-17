package com.gabriel0011.asesmenmobpro.ui.screen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gabriel0011.asesmenmobpro.model.HistoryDao
import com.gabriel0011.asesmenmobpro.model.HistoryEntity
import com.gabriel0011.asesmenmobpro.network.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

enum class ApiStatus { LOADING, SUCCESS, FAILED }

class HistoryViewModel(private val dao: HistoryDao) : ViewModel() {
    var status by mutableStateOf(ApiStatus.SUCCESS)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    val data: StateFlow<List<HistoryEntity>> = dao.getAllHistory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    fun fetchData(userEmail: String) {
        if (userEmail.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            status = ApiStatus.LOADING
            try {
                Log.d("SYNC_DEBUG", "Memulai sinkronisasi untuk: $userEmail")
                val remoteData = ApiConfig.getApiService().getHistory()
                val myRemoteData = remoteData.filter { it.userEmail == userEmail }

                dao.clearHistoryByEmail(userEmail)
                myRemoteData.forEach { dao.insertHistory(it) }

                status = ApiStatus.SUCCESS
                errorMessage = null
            } catch (e: Exception) {
                Log.e("SYNC_DEBUG", "Gagal fetch: ${e.message}")
                errorMessage = "Gagal sinkronisasi data: ${e.localizedMessage}"
                status = ApiStatus.FAILED
            }
        }
    }

    fun uploadAndInsertHistory(
        namaLatihan: String, berat: String, repetisi: String,
        hasil1RM: String, tanggal: String, satuan: String,
        userEmail: String, imageUri: Uri?, appContext: Context
    ) {
        if (userEmail.isEmpty()) {
            errorMessage = "Silakan login terlebih dahulu di menu profil!"
            status = ApiStatus.FAILED
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            status = ApiStatus.LOADING
            errorMessage = null
            try {
                var finalImageUrl = ""

                if (imageUri != null) {
                    val inputStream = appContext.contentResolver.openInputStream(imageUri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {
                        val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        val multipartFile =
                            MultipartBody.Part.createFormData("file", "upload.jpg", requestFile)

                        val presetBody = "gymmax_bebas".toRequestBody("text/plain".toMediaTypeOrNull())

                        val cloudinaryResult = ApiConfig.getApiService().uploadImageToCloudinary(
                            file = multipartFile,
                            uploadPreset = presetBody
                        )

                        finalImageUrl = cloudinaryResult.secureUrl
                    }
                }

                val newHistory = HistoryEntity(
                    namaLatihan = namaLatihan,
                    berat = berat,
                    repetisi = repetisi,
                    hasil1RM = hasil1RM,
                    tanggal = tanggal,
                    satuan = satuan,
                    userEmail = userEmail,
                    imageUrl = finalImageUrl
                )

                val response = ApiConfig.getApiService().insertHistory(newHistory)

                if (response.isSuccessful) {
                    val remoteSaved = response.body()
                    if (remoteSaved != null) {
                        dao.insertHistory(remoteSaved)
                        status = ApiStatus.SUCCESS
                    } else {
                        throw java.lang.Exception("Data dari server kosong")
                    }
                } else {
                    throw java.lang.Exception("Gagal di server, kode HTTP: ${response.code()}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Gagal menyimpan: ${e.localizedMessage}"
                status = ApiStatus.FAILED
            }
        }
    }

    fun updateHistoryWithImage(context: Context, imageUri: Uri?, entity: HistoryEntity) {
        status = ApiStatus.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var finalImageUrl = entity.imageUrl

                if (imageUri != null) {
                    val inputStream = context.contentResolver.openInputStream(imageUri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {
                        val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        val multipartFile = MultipartBody.Part.createFormData("file", "upload.jpg", requestFile)
                        val presetBody = "gymmax_bebas".toRequestBody("text/plain".toMediaTypeOrNull())

                        val uploadResponse = ApiConfig.getApiService().uploadImageToCloudinary(multipartFile, presetBody)
                        finalImageUrl = uploadResponse.secureUrl
                    }
                }

                val finalEntity = entity.copy(imageUrl = finalImageUrl)
                val response = ApiConfig.getApiService().updateHistory(finalEntity.id, finalEntity)

                if (response.isSuccessful) {
                    dao.updateHistory(finalEntity)
                    status = ApiStatus.SUCCESS
                    Log.d("UPDATE_DEBUG", "Berhasil update di Server dan Lokal!")
                } else {
                    errorMessage = "Gagal menyimpan ke server"
                    status = ApiStatus.FAILED
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Error jaringan: ${e.message}"
                status = ApiStatus.FAILED
            }
        }
    }

    fun deleteHistory(id: Long) {
        status = ApiStatus.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiConfig.getApiService().deleteHistory(id)

                if (response.isSuccessful) {
                    dao.deleteHistoryById(id)
                    status = ApiStatus.SUCCESS
                    Log.d("DELETE_DEBUG", "Berhasil hapus di API dan Lokal")
                } else {
                    errorMessage = "Gagal menghapus di server"
                    status = ApiStatus.FAILED
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Error: ${e.message}"
                status = ApiStatus.FAILED
            }
        }
    }

    fun getHistory(id: Long, onResult: (HistoryEntity?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = dao.getHistoryById(id)
            onResult(result)
        }
    }

    companion object {
        fun factory(dao: HistoryDao) = viewModelFactory {
            initializer { HistoryViewModel(dao) }
        }
    }
}