package com.gabriel0011.asesmenmobpro.ui.screen

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
                val remoteData = ApiConfig.getApiService().getHistory()
                val myRemoteData = remoteData.filter { it.userEmail == userEmail }

                dao.clearHistoryByEmail(userEmail)
                myRemoteData.forEach { dao.insertHistory(it) }

                status = ApiStatus.SUCCESS
            } catch (e: Exception) {
                errorMessage = "Gagal sinkronisasi data: ${e.message}"
                status = ApiStatus.FAILED
            }
        }
    }

    fun insertHistory(namaLatihan: String, berat: String, repetisi: String, hasil1RM: String, tanggal: String, satuan: String, userEmail: String = "", imageUrl: String = "") {
        Thread {
            val newHistory = HistoryEntity(
                namaLatihan = namaLatihan, berat = berat, repetisi = repetisi,
                hasil1RM = hasil1RM, tanggal = tanggal, satuan = satuan,
                userEmail = userEmail, imageUrl = imageUrl
            )
            dao.insertHistory(newHistory)
        }.start()
    }

    fun updateHistory(entity: HistoryEntity) {
        Thread { dao.updateHistory(entity) }.start()
    }

    fun deleteHistory(id: Long) {
        Thread { dao.deleteHistoryById(id) }.start()
    }

    fun getHistory(id: Long, onResult: (HistoryEntity?) -> Unit) {
        Thread {
            val result = dao.getHistoryById(id)
            onResult(result)
        }.start()
    }

    companion object {
        fun factory(dao: HistoryDao) = viewModelFactory {
            initializer { HistoryViewModel(dao) }
        }
    }
}